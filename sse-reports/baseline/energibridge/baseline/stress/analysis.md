# EnergiBridge analysis

## Series `baseline/stress`

### Idle reference
- idle_start: 494.0 J in 311.0 s = 1.59 W (mean CPU usage 3.8 %)
- idle_end: 528.7 J in 311.0 s = 1.70 W (mean CPU usage 4.1 %)
- Reference idle power (mean of available): **1.64 W**
- Idle drift (end vs start): +7.0 %

### Per run

| Run | Energy (J) | Duration (s) | Power (W) | CPU % | Incr. (J) | Requests OK | mJ/req (incr.) | Status |
|---|---|---|---|---|---|---|---|---|
| run01 | 757.4 | 303.9 | 2.49 | 8.3 | 257.5 | 18137 | 14.20 | OK |
| run02 | 775.3 | 303.9 | 2.55 | 8.1 | 275.4 | 18147 | 15.18 | OK |
| run03 | 746.8 | 304.9 | 2.45 | 7.9 | 245.4 | 18129 | 13.53 | OK |
| run04 | 766.1 | 303.9 | 2.52 | 8.1 | 266.3 | 18133 | 14.69 | OK |
| run05 | 751.6 | 303.9 | 2.47 | 7.9 | 251.8 | 18122 | 13.89 | OK |
| run06 | 725.3 | 303.9 | 2.39 | 7.7 | 225.5 | 18117 | 12.45 | OK |
| run07 | 788.9 | 303.9 | 2.60 | 8.5 | 289.1 | 18146 | 15.93 | OK |
| run08 | 805.7 | 304.9 | 2.64 | 8.7 | 304.3 | 18153 | 16.76 | OK |
| run09 | 778.9 | 303.9 | 2.56 | 8.2 | 279.1 | 18138 | 15.39 | OK |
| run10 | 768.9 | 304.9 | 2.52 | 8.1 | 267.4 | 18141 | 14.74 | OK |

### Summary over 10 of 10 runs (invalid runs excluded)

| Metric | Median | Mean | SD | Min | Max | CV % |
|---|---|---|---|---|---|---|
| Total CPU energy (J) | 767.5 | 766.5 | 22.8 | 725.3 | 805.7 | 3.0 |
| Incremental energy = total - idle power x duration (J) | 266.9 | 266.2 | 22.6 | 225.5 | 304.3 | 8.5 |
| Duration (s) | 303.9 | 304.2 | 0.5 | 303.9 | 304.9 | 0.2 |
| Mean power (W) | 2.52 | 2.52 | 0.07 | 2.39 | 2.64 | 2.9 |
| Successful requests | 18138 | 18136 | 11 | 18117 | 18153 | 0.1 |
| Total energy per request (mJ) | 42.32 | 42.26 | 1.23 | 40.03 | 44.39 | 2.9 |
| Incremental energy per request (mJ) | 14.71 | 14.68 | 1.24 | 12.45 | 16.76 | 8.4 |
