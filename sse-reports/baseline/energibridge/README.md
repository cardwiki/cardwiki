# EnergiBridge – Baseline energy measurements

Software-based CPU energy measurements of the **baseline** version of CardWiki
under the team's JMeter workloads (`load` and `stress`), taken with
[EnergiBridge](https://github.com/tdurieux/EnergiBridge). These results are the
reference for the Blue Angel criterion: an update must not increase energy
consumption by more than 10 % compared to this baseline.

## Analysed version

| Item | Value |
|---|---|
| Branch measured | `feature/energibridge-baseline` (created from `baseline`, no code changes) |
| Commit | `7f3462b10b868990cb518d2448826aff041101b9` |
| Application code | Identical to commit `4cd6c67` used in the team's JMeter reports |
| Backend profile | `dev` (persistent H2 file database, not regenerated between series) |
| Measurement date | 2026-11-03 (stress 10:25, load 13:43, CET) |

## Measurement environment

| Item | Value |
|---|---|
| Machine | Laptop, AMD Ryzen 5 7520U with Radeon Graphics |
| OS | Windows 11 Home 10.0.26300 |
| Power scheme | Balanced (`381b4222-f694-41f0-9685-ff5bb260df2e`) |
| EnergiBridge | 0.0.7, sampling interval 1000 ms |
| Java | OpenJDK 11.0.32.1 |
| JMeter | 5.6.3, non-GUI mode, running on the same machine |

Conditions during both series:

- All other applications closed (only the backend, JMeter and the terminal running the script).
- OneDrive synchronisation and Windows Update paused; sleep disabled.
- The same backend process served both series (no restart between `stress` and `load`).

The exact values recorded by the script are in each series' `environment.txt`.

## Procedure

Each series is driven by [`energibridge_run.ps1`](energibridge_run.ps1)
(`-Label baseline -Plan stress|load -Runs 10`):

1. Check that the EnergiBridge driver service (`rapl`) is running and the backend answers on `http://localhost:8080`.
2. One unmeasured JMeter warm-up run (JIT, caches, DB pages).
3. `idle_start`: system idle measurement lasting as long as one run (310 s).
4. 10 measured runs. Each run is preceded by a 30 s cool-down, and EnergiBridge wraps the whole JMeter process (`energibridge -i 1000 --summary -o runNN.csv -- jmeter ...`).
5. `idle_end`: second idle measurement, to detect drift.

Workloads (JMeter plans from `jmeter/` on `master`, common properties `think_ms=500 search=Haupt deck_id=1`):

| Plan | Properties | Workload duration |
|---|---|---|
| `load` | 20 users, ramp-up 10 s, 300 s | 300 s |
| `stress` | 5 users for 60 s → 50 users for 180 s → 5 users for 60 s (ramp-ups 5 s / 10 s) | 300 s |

**Quality control.** After every measurement the script computes the average
CPU power in consecutive 30 s windows. If any window exceeds 5.5 W (a sign of
background activity, as seen in discarded earlier attempts with 8–12 W bursts),
the measurement is moved to `rejected/` and repeated, up to 3 attempts. The script
also records the CPU time consumed by the 8 busiest processes during each
measurement (`*.procs.txt`), so that background interference can be checked
afterwards.

In both series, **all 10 runs and both idle measurements were accepted on the
first attempt**; `rejected/` stayed empty and is therefore not in the repository.

## Analysis

[`analyze_energibridge.py`](analyze_energibridge.py) (Python standard library only):

- Energy = last − first value of the cumulative counter `CPU_ENERGY (J)` (not a sum of power samples), cross-checked against EnergiBridge's `--summary` output.
- Incremental energy = total energy − reference idle power × duration. The reference idle power is the mean of `idle_start` and `idle_end`.
- Successful requests are counted from the run's `.jtl` within the EnergiBridge time window.
- A run is invalid if the counter resets, the CSV is truncated, `--summary` is missing, or more than 0.1 % of requests fail.

```bash
python analyze_energibridge.py baseline/stress --out baseline/stress/analysis.md
python analyze_energibridge.py baseline/load --out baseline/load/analysis.md
```

> The `.jtl` files are stored compressed as `jtl.zip` in each series folder
> (≈ 25 MB uncompressed per series). **Extract `jtl.zip` into the series folder
> before re-running the analysis**, otherwise every run is reported as invalid
> (`no .jtl file`).

## Results

Medians over 10 of 10 valid runs. Full tables: [`baseline/stress/analysis.md`](baseline/stress/analysis.md), [`baseline/load/analysis.md`](baseline/load/analysis.md).

| Metric | `stress` | `load` |
|---|---|---|
| Total CPU energy per run (J) | **767.5** (CV 3.0 %) | **657.0** (CV 3.4 %) |
| Mean power (W) | 2.52 | 2.16 |
| Reference idle power (W) | 1.64 | 1.56 |
| Idle drift (`idle_end` vs `idle_start`) | +7.0 % | −1.8 % |
| Incremental energy (J) | 266.9 (CV 8.5 %) | 182.2 (CV 12.2 %) |
| Successful requests per run | 18 138 | 11 500 |
| Total energy per request (mJ) | **42.32** | **57.04** |
| Incremental energy per request (mJ) | 14.71 | 15.82 |

Observations:

- Both series are stable (CV ≈ 3 % on total energy), well below the 10 % Blue Angel margin, so a later comparison can detect differences of that size.
- Idle power accounts for roughly two thirds of the total energy of a run. The incremental energy is the part attributable to the workload, but its relative dispersion is higher because it is a difference of two measured values.
- `stress` processes about 58 % more requests than `load` for only 17 % more energy, so the energy per request is lower under higher load (better amortisation of the idle baseline).
- In the process logs, `java` (backend and JMeter) is the largest CPU consumer during runs. The remaining CPU time comes from usual Windows services (`svchost`, `dwm`, `MsMpEng`...).

**Values to use as reference for the improved version:** total energy per run
and energy per request, per plan. Use the same machine, plans, number of runs and
script (`-Label improved`, output in `sse-reports/sse-improvements/energibridge/`),
then compare:

```bash
python analyze_energibridge.py baseline/stress --compare ../../sse-improvements/energibridge/improved/stress
```

## Files

```
energibridge/
├── README.md
├── energibridge_run.ps1        measurement script (Windows PowerShell)
├── analyze_energibridge.py     analysis script
└── baseline/
    ├── stress/  load/
    │   ├── environment.txt     machine, versions, plan parameters, commit
    │   ├── analysis.md         generated report
    │   ├── idle_start.* idle_end.*
    │   ├── runNN.csv           EnergiBridge samples (1 s)
    │   ├── runNN.summary.txt   EnergiBridge --summary output
    │   ├── runNN.procs.txt     CPU time of the 8 busiest processes
    │   └── jtl.zip             JMeter results (runNN.jtl, warmup.jtl)
```

## Limitations

- **Platform.** EnergiBridge on Windows reads the CPU energy counters through the `LibreHardwareMonitor.sys` (WinRing0) kernel driver. Microsoft Defender flags it as `VulnerableDriver:WinNT/Winring0`, so it was only installed for the measurements and must be removed afterwards. The course material covers EnergiBridge on macOS and Linux only; the Windows setup is not part of it.
- **CPU energy only.** Only the CPU package counter (`CPU_ENERGY`) is measured. Display, memory, disk, network and the rest of the platform are not included.
- **Whole-system measurement.** EnergiBridge measures the whole machine, not the application. JMeter runs on the same machine, so its energy is included. Background activity is mitigated (closed apps, paused services, quality control) but not eliminated.
- **Sampling interval.** The default 200 ms interval made EnergiBridge crash under load, so 1000 ms is used. Totals come from the cumulative counter and are unaffected; only the time resolution is coarser.
- **Persistent database.** The `dev` H2 database is a persistent file and grows slightly between runs. It was not reset between series, and the same must apply to the improved series for comparability.
- **Single machine, single day.** The results are valid as a relative reference on this laptop. They are not absolute energy figures for a production deployment.

## Discarded attempts (not included)

- A first `stress` series showed CV ≈ 17 % with 8–12 W bursts caused by background activity. This motivated the 30 s quality control described above.
- A `stress` attempt was aborted after a reboot because idle power was ≈ 10 W.
