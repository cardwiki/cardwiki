# EnergiBridge analysis

## Series `baseline/load`

### Idle reference
- idle_start: 489.4 J in 311.0 s = 1.57 W (mean CPU usage 3.9 %)
- idle_end: 480.6 J in 311.0 s = 1.55 W (mean CPU usage 3.7 %)
- Reference idle power (mean of available): **1.56 W**
- Idle drift (end vs start): -1.8 %

### Per run

| Run | Energy (J) | Duration (s) | Power (W) | CPU % | Incr. (J) | Requests OK | mJ/req (incr.) | Status |
|---|---|---|---|---|---|---|---|---|
| run01 | 631.9 | 303.9 | 2.08 | 6.9 | 157.9 | 11489 | 13.74 | OK |
| run02 | 676.1 | 304.9 | 2.22 | 7.3 | 200.6 | 11507 | 17.43 | OK |
| run03 | 654.8 | 303.9 | 2.15 | 7.0 | 180.7 | 11515 | 15.70 | OK |
| run04 | 665.6 | 303.9 | 2.19 | 7.2 | 191.6 | 11482 | 16.69 | OK |
| run05 | 637.3 | 303.9 | 2.10 | 6.9 | 163.3 | 11503 | 14.20 | OK |
| run06 | 703.3 | 304.9 | 2.31 | 7.6 | 227.6 | 11498 | 19.80 | OK |
| run07 | 641.2 | 305.0 | 2.10 | 6.8 | 165.6 | 11494 | 14.41 | OK |
| run08 | 630.1 | 303.9 | 2.07 | 6.9 | 156.1 | 11493 | 13.58 | OK |
| run09 | 662.4 | 304.9 | 2.17 | 7.0 | 186.8 | 11509 | 16.23 | OK |
| run10 | 659.2 | 304.9 | 2.16 | 7.1 | 183.6 | 11520 | 15.94 | OK |

### Summary over 10 of 10 runs (invalid runs excluded)

| Metric | Median | Mean | SD | Min | Max | CV % |
|---|---|---|---|---|---|---|
| Total CPU energy (J) | 657.0 | 656.2 | 22.6 | 630.1 | 703.3 | 3.4 |
| Incremental energy = total - idle power x duration (J) | 182.2 | 181.4 | 22.1 | 156.1 | 227.6 | 12.2 |
| Duration (s) | 304.4 | 304.4 | 0.5 | 303.9 | 305.0 | 0.2 |
| Mean power (W) | 2.16 | 2.16 | 0.07 | 2.07 | 2.31 | 3.3 |
| Successful requests | 11500 | 11501 | 12 | 11482 | 11520 | 0.1 |
| Total energy per request (mJ) | 57.04 | 57.06 | 1.96 | 54.83 | 61.16 | 3.4 |
| Incremental energy per request (mJ) | 15.82 | 15.77 | 1.92 | 13.58 | 19.80 | 12.2 |
