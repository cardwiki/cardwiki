#!/usr/bin/env python3
"""
Analysis of an EnergiBridge measurement series (CardWiki SSE project).

Expected files in the series directory (produced by energibridge_run.ps1):
  idle_start.csv, idle_end.csv          idle (no workload) measurements
  run01.csv, run01.jtl, run01.summary.txt, run02.csv, ...

Energy is computed from the cumulative counter 'CPU_ENERGY (J)' (last - first
sample), NOT by summing power. Only CPU energy is measured (not display, disk...).

Usage:
  python analyze_energibridge.py <series_dir>                      # one series
  python analyze_energibridge.py <dirA> --compare <dirB>           # A=baseline, B=improved
  python analyze_energibridge.py <series_dir> --profile run04,run07   # power per 30 s bins
  options: --include-all (keep runs flagged as invalid), --out FILE (save report)

Only the Python standard library is needed.
"""
import argparse, csv, glob, os, random, re, statistics as st, sys

E_COL = "CPU_ENERGY (J)"
SUMMARY_RE = re.compile(r"Energy consumption in joules:\s*([\d.]+)\s*for\s*([\d.]+)")
DRIFT_WARN_PCT = 15.0      # idle_end vs idle_start
SUMMARY_TOL_PCT = 5.0      # counter-based energy vs EnergiBridge --summary
DURATION_TOL_PCT = 5.0     # CSV duration vs --summary duration (truncation check)
ERROR_RATE_MAX = 0.001     # fraction of failed requests tolerated
SWAP_GROWTH_MB = 200.0
OUTLIER_PCT = 20.0         # warn if a run's mean power deviates this much from the median


# ---------------------------------------------------------------- reading
def read_eb(path):
    with open(path, newline="") as f:
        rows = list(csv.DictReader(f))
    if len(rows) < 2 or E_COL not in rows[0]:
        raise ValueError(f"{path}: not a valid EnergiBridge CSV (no '{E_COL}' column)")
    return rows


def eb_metrics(rows):
    """Energy (J) from the cumulative counter, duration (s), mean CPU %, swap growth (MB)."""
    prev, energy, negatives = None, 0.0, 0
    for r in rows:
        v = float(r[E_COL])
        if prev is not None:
            d = v - prev
            if d >= 0:
                energy += d
            else:
                negatives += 1           # counter reset / wrap-around
        prev = v
    duration = sum(float(r["Delta"]) for r in rows) / 1000.0
    cpu_cols = [c for c in rows[0] if c.startswith("CPU_USAGE_")]
    cpu = st.mean(st.mean(float(r[c]) for c in cpu_cols) for r in rows) if cpu_cols else float("nan")
    swap = (float(rows[-1]["USED_SWAP"]) - float(rows[0]["USED_SWAP"])) / 1e6 if "USED_SWAP" in rows[0] else 0.0
    return energy, duration, cpu, swap, negatives


def read_summary(path):
    if not os.path.exists(path):
        return None
    with open(path, "rb") as f:
        raw = f.read()
    if raw[:2] in (b"\xff\xfe", b"\xfe\xff") or b"\x00" in raw[:200]:
        text = raw.decode("utf-16", errors="ignore")      # Windows PowerShell 5.1 Tee-Object
    else:
        text = raw.decode("utf-8-sig", errors="ignore")
    m = SUMMARY_RE.findall(text)
    return (float(m[-1][0]), float(m[-1][1])) if m else None


def count_requests(jtl, t0, t1):
    """Count successful/failed requests whose timeStamp falls in the EnergiBridge window.
    Samples outside it (JMeter appends to an existing .jtl on retries) are ignored."""
    ok = fail = outside = 0
    if not os.path.exists(jtl):
        return None
    with open(jtl, newline="") as f:
        for r in csv.DictReader(f):
            ts = int(r["timeStamp"])
            if t0 - 2000 <= ts <= t1 + 2000:
                if r["success"].strip().lower() == "true":
                    ok += 1
                else:
                    fail += 1
            else:
                outside += 1
    return ok, fail, outside


def load_series(d):
    idle = {}
    for name in ("idle_start", "idle_end"):
        p = os.path.join(d, name + ".csv")
        if os.path.exists(p):
            e, t, cpu, swap, neg = eb_metrics(read_eb(p))
            idle[name] = {"E": e, "t": t, "P": e / t if t else float("nan"), "cpu": cpu}
    p_idle = st.mean(v["P"] for v in idle.values()) if idle else None
    drift = None
    if "idle_start" in idle and "idle_end" in idle:
        drift = (idle["idle_end"]["P"] / idle["idle_start"]["P"] - 1) * 100

    runs = []
    for csv_path in sorted(glob.glob(os.path.join(d, "run*.csv"))):
        stem = csv_path[:-4]
        name = os.path.basename(stem)
        rows = read_eb(csv_path)
        e, t, cpu, swap, neg = eb_metrics(rows)
        summ = read_summary(stem + ".summary.txt")
        req = count_requests(stem + ".jtl", int(rows[0]["Time"]), int(rows[-1]["Time"]))
        bad, warn = [], []
        if neg:
            bad.append(f"energy counter reset {neg}x")
        if e <= 0:
            bad.append("no energy measured")
        if summ is None:
            bad.append("no --summary line (EnergiBridge crashed?)")
        else:
            if abs(t - summ[1]) / summ[1] * 100 > DURATION_TOL_PCT:
                bad.append(f"CSV truncated ({t:.0f}s vs {summ[1]:.0f}s)")
            if abs(e - summ[0]) / summ[0] * 100 > SUMMARY_TOL_PCT:
                warn.append(f"counter {e:.1f} J vs summary {summ[0]:.1f} J")
        if req is None:
            bad.append("no .jtl file")
            ok = fail = 0
        else:
            ok, fail, outside = req
            if ok == 0:
                bad.append("no successful requests in window")
            elif fail / (ok + fail) > ERROR_RATE_MAX:
                bad.append(f"{fail} failed requests")
            if outside > 0.02 * (ok + fail):
                warn.append(f"{outside} jtl rows outside window ignored (retry?)")
        if swap > SWAP_GROWTH_MB:
            warn.append(f"swap grew {swap:.0f} MB")
        run = {"run": name, "E_total": e, "t": t, "P": e / t if t else float("nan"),
               "cpu": cpu, "n_ok": ok, "n_fail": fail, "bad": bad, "warn": warn}
        if p_idle is not None:
            run["E_incr"] = e - p_idle * t
        run["mJ_req_total"] = e / ok * 1000 if ok else float("nan")
        if p_idle is not None and ok:
            run["mJ_req_incr"] = (e - p_idle * t) / ok * 1000
        runs.append(run)
    good = [r["P"] for r in runs if not r["bad"]]
    if len(good) >= 4:
        med = st.median(good)
        for r in runs:
            if not r["bad"] and med and abs(r["P"] / med - 1) > OUTLIER_PCT / 100:
                r["warn"].append(f"power {r['P']:.2f} W is {(r['P'] / med - 1) * 100:+.0f} % vs median {med:.2f} W")
    return {"dir": d, "label": "/".join(os.path.normpath(d).split(os.sep)[-2:]),
            "idle": idle, "p_idle": p_idle, "drift": drift, "runs": runs}


# ---------------------------------------------------------------- stats
def desc(vals):
    n = len(vals)
    if n == 0:
        return None
    m = st.mean(vals)
    sd = st.stdev(vals) if n > 1 else float("nan")
    return {"n": n, "median": st.median(vals), "mean": m, "sd": sd,
            "min": min(vals), "max": max(vals), "cv": sd / m * 100 if n > 1 and m else float("nan")}


def perm_test(a, b, n=10000, seed=1):
    rnd = random.Random(seed)
    obs = abs(st.median(b) - st.median(a))
    pool, na, c = list(a) + list(b), len(a), 0
    for _ in range(n):
        rnd.shuffle(pool)
        if abs(st.median(pool[na:]) - st.median(pool[:na])) >= obs - 1e-12:
            c += 1
    return (c + 1) / (n + 1)


def boot_ci(a, b, n=5000, seed=2):
    rnd, out = random.Random(seed), []
    for _ in range(n):
        ra, rb = st.median(rnd.choices(a, k=len(a))), st.median(rnd.choices(b, k=len(b)))
        if ra:
            out.append((rb - ra) / ra * 100)
    out.sort()
    return out[int(0.025 * len(out))], out[int(0.975 * len(out)) - 1]


def cohen_d(a, b):
    if len(a) < 2 or len(b) < 2:
        return float("nan")
    sp = (((len(a) - 1) * st.variance(a) + (len(b) - 1) * st.variance(b)) / (len(a) + len(b) - 2)) ** 0.5
    return (st.mean(b) - st.mean(a)) / sp if sp else float("nan")


# ---------------------------------------------------------------- report
METRICS = [("E_total", "Total CPU energy (J)", "{:.1f}"),
           ("E_incr", "Incremental energy = total - idle power x duration (J)", "{:.1f}"),
           ("t", "Duration (s)", "{:.1f}"),
           ("P", "Mean power (W)", "{:.2f}"),
           ("n_ok", "Successful requests", "{:.0f}"),
           ("mJ_req_total", "Total energy per request (mJ)", "{:.2f}"),
           ("mJ_req_incr", "Incremental energy per request (mJ)", "{:.2f}")]


def select(series, include_all):
    return [r for r in series["runs"] if include_all or not r["bad"]]


def report_series(s, include_all):
    L = [f"## Series `{s['label']}`", ""]
    L.append("### Idle reference")
    for k, v in s["idle"].items():
        L.append(f"- {k}: {v['E']:.1f} J in {v['t']:.1f} s = {v['P']:.2f} W (mean CPU usage {v['cpu']:.1f} %)")
    if s["p_idle"] is None:
        L.append("- **No idle measurements found: incremental energy cannot be computed.**")
    else:
        L.append(f"- Reference idle power (mean of available): **{s['p_idle']:.2f} W**")
    if s["drift"] is not None:
        flag = " **WARNING: drift above threshold**" if abs(s["drift"]) > DRIFT_WARN_PCT else ""
        L.append(f"- Idle drift (end vs start): {s['drift']:+.1f} %{flag}")
    L += ["", "### Per run", "", "| Run | Energy (J) | Duration (s) | Power (W) | CPU % | Incr. (J) | Requests OK | mJ/req (incr.) | Status |",
          "|---|---|---|---|---|---|---|---|---|"]
    for r in s["runs"]:
        status = "INVALID: " + "; ".join(r["bad"]) if r["bad"] else ("OK" + (" (" + "; ".join(r["warn"]) + ")" if r["warn"] else ""))
        L.append("| {run} | {E_total:.1f} | {t:.1f} | {P:.2f} | {cpu:.1f} | {inc} | {n_ok} | {mj} | {status} |".format(
            inc=f"{r['E_incr']:.1f}" if "E_incr" in r else "n/a", mj=f"{r['mJ_req_incr']:.2f}" if "mJ_req_incr" in r else "n/a",
            status=status, **r))
    valid = select(s, include_all)
    L += ["", f"### Summary over {len(valid)} of {len(s['runs'])} runs" + (" (all runs included)" if include_all else " (invalid runs excluded)"), "",
          "| Metric | Median | Mean | SD | Min | Max | CV % |", "|---|---|---|---|---|---|---|"]
    for key, title, fmt in METRICS:
        d = desc([r[key] for r in valid if key in r])
        if d:
            L.append(f"| {title} | {fmt.format(d['median'])} | {fmt.format(d['mean'])} | {fmt.format(d['sd'])} | "
                     f"{fmt.format(d['min'])} | {fmt.format(d['max'])} | {d['cv']:.1f} |")
    return L


def report_compare(a, b, include_all):
    ra, rb = select(a, include_all), select(b, include_all)
    L = [f"## Comparison: `{a['label']}` (A, reference) vs `{b['label']}` (B)", "",
         f"Valid runs: A={len(ra)}, B={len(rb)}. Differences are B relative to A (negative = B uses less).", ""]
    if a["p_idle"] and b["p_idle"]:
        gap = (b["p_idle"] / a["p_idle"] - 1) * 100
        L.append(f"Idle power: A={a['p_idle']:.2f} W, B={b['p_idle']:.2f} W ({gap:+.1f} %)"
                 + (" **WARNING: measurement conditions differ between series.**" if abs(gap) > DRIFT_WARN_PCT else ""))
        L.append("")
    L += ["| Metric | Median A | Median B | Change % | 95 % CI (bootstrap) | p (permutation) | Cohen d | Verdict |",
          "|---|---|---|---|---|---|---|---|"]
    for key, title, fmt in METRICS:
        if key == "n_ok":
            continue
        va, vb = [r[key] for r in ra if key in r], [r[key] for r in rb if key in r]
        if len(va) < 2 or len(vb) < 2:
            continue
        ma, mb = st.median(va), st.median(vb)
        ch = (mb - ma) / ma * 100 if ma else float("nan")
        lo, hi = boot_ci(va, vb)
        p = perm_test(va, vb)
        verdict = ("no significant difference" if (lo <= 0 <= hi or p >= 0.05)
                   else ("B uses less" if mb < ma else "B uses MORE"))
        d = cohen_d(va, vb)
        ds = f"{d:+.2f}" if d == d else "n/a"
        L.append(f"| {title} | {fmt.format(ma)} | {fmt.format(mb)} | {ch:+.1f} | [{lo:+.1f}, {hi:+.1f}] | {p:.3f} | "
                 f"{ds} | {verdict} |")
    ea, eb = [r["E_total"] for r in ra], [r["E_total"] for r in rb]
    if ea and eb:
        inc = (st.median(eb) / st.median(ea) - 1) * 100
        L += ["", f"Blue Angel criterion (total energy must not increase by more than 10 % vs the reference): "
                  f"change = {inc:+.1f} % -> " + ("**EXCEEDS 10 %**" if inc > 10 else "within the limit") + "."]
    L += ["", "Notes: the comparison is only valid if both series use the same plan, workload, machine and conditions, "
              "and the same number of runs. Only CPU energy is measured."]
    return L


def profile(d, names, bin_s=30):
    """Mean power (W) and CPU % in consecutive bins of bin_s seconds, side by side for several runs."""
    series = {}
    for name in names:
        rows = read_eb(os.path.join(d, name + ".csv"))
        bins, prev = [], float(rows[0][E_COL])
        acc_t = acc_e = 0.0
        cpus = []
        cpu_cols = [c for c in rows[0] if c.startswith("CPU_USAGE_")]
        for r in rows[1:]:
            v = float(r[E_COL])
            acc_t += float(r["Delta"]) / 1000.0
            acc_e += max(0.0, v - prev)
            prev = v
            cpus.append(st.mean(float(r[c]) for c in cpu_cols))
            if acc_t >= bin_s:
                bins.append((acc_e / acc_t, st.mean(cpus)))
                acc_t = acc_e = 0.0
                cpus = []
        if acc_t >= bin_s / 2:
            bins.append((acc_e / acc_t, st.mean(cpus)))
        series[name] = bins
    head = "| Start (s) | " + " | ".join(f"{n} W | {n} CPU %" for n in names) + " |"
    L = [f"## Power profile per {bin_s} s", "", head, "|---" * (1 + 2 * len(names)) + "|"]
    for i in range(max(len(b) for b in series.values())):
        cells = []
        for n in names:
            b = series[n]
            cells.append(f"{b[i][0]:.2f} | {b[i][1]:.1f}" if i < len(b) else " | ")
        L.append(f"| {i * bin_s} | " + " | ".join(cells) + " |")
    return L


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("series_dir")
    ap.add_argument("--compare", metavar="DIR_B")
    ap.add_argument("--include-all", action="store_true")
    ap.add_argument("--profile", metavar="RUNS", help="comma-separated runs, e.g. run04,run07: power per 30 s bins")
    ap.add_argument("--out")
    args = ap.parse_args()

    if args.profile:
        print("\n".join(profile(args.series_dir, [x.strip() for x in args.profile.split(",")])))
        return
    a = load_series(args.series_dir)
    if not a["runs"]:
        sys.exit(f"No run*.csv files in {args.series_dir}")
    lines = ["# EnergiBridge analysis", ""] + report_series(a, args.include_all)
    if args.compare:
        b = load_series(args.compare)
        if not b["runs"]:
            sys.exit(f"No run*.csv files in {args.compare}")
        lines += [""] + report_series(b, args.include_all) + [""] + report_compare(a, b, args.include_all)
    text = "\n".join(lines)
    print(text)
    out = args.out or os.path.join(args.series_dir, "analysis.md")
    with open(out, "w", encoding="utf-8") as f:
        f.write(text + "\n")
    print(f"\n[saved to {out}]")


if __name__ == "__main__":
    main()
