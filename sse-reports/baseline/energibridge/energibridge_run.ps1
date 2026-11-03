param(
  [ValidateSet("baseline","improved")][string]$Label = "baseline",
  [ValidateSet("load","stress")][string]$Plan = "load",
  [int]$Runs = 10,
  [int]$Interval = 1000,
  [double]$MaxBinWatts = 5.5,
  [switch]$Smoke,
  [string]$Eb = "C:\tools\energibridge\energibridge.exe",
  [string]$Jmeter = "C:\Users\Asus\Downloads\apache-jmeter-5.6.3\apache-jmeter-5.6.3\bin\jmeter.bat"
)
$ErrorActionPreference = "Stop"
if ((Get-Service rapl).Status -ne "Running") { throw "Driver 'rapl' is not running" }
Invoke-RestMethod "http://localhost:8080/api/v1/decks/1" | Out-Null   # backend alive?

if ($Plan -eq "load") {
  $d = if ($Smoke) { 30 } else { 300 }
  $planProps = @("-Jusers=20","-Jramp_up=10","-Jduration=$d")
  $total = $d
} else {
  $main = if ($Smoke) { 20 } else { 180 }
  $base = if ($Smoke) { 10 } else { 60 }
  $planProps = @("-Jusers=50","-Jramp_up=10","-Jduration=$main",
                 "-Jbase_users=5","-Jbase_ramp_up=5","-Jbase_duration=$base")
  $total = $main + 2 * $base
}
$window = $total + 10

$root = if ($Label -eq "baseline") { "sse-reports/baseline" } else { "sse-reports/sse-improvements" }
$out  = "$root/energibridge/$Label/$Plan"
if ($Smoke) { $out += "_smoke" }
New-Item -ItemType Directory -Force $out | Out-Null
New-Item -ItemType Directory -Force "$out/rejected" | Out-Null

@(
  "date: $(Get-Date -Format o)",
  "checkout commit: $(git rev-parse HEAD)",
  "branch: $(git branch --show-current)",
  "cpu: $((Get-CimInstance Win32_Processor).Name)",
  "os: $((Get-CimInstance Win32_OperatingSystem).Caption) $((Get-CimInstance Win32_OperatingSystem).Version)",
  "power scheme: $(powercfg /getactivescheme)",
  "energibridge: $(& $Eb --version)",
  "energibridge sampling interval: $Interval ms (default 200 ms crashed under load)",
  "quality control: a measurement is repeated (max 3 attempts) if any 30 s window exceeds $MaxBinWatts W; rejected attempts are kept in rejected/",
  "java: $(cmd /c 'java -version 2>&1' | Select-Object -First 1)",
  "plan: ${Plan}_test.jmx $($planProps -join ' ') think_ms=500 search=Haupt deck_id=1",
  "runs: $Runs, cool-down 30 s, idle ${window} s at start and end, smoke=$Smoke"
) | Set-Content "$out/environment.txt"

$jm = @("-n","-t","jmeter/${Plan}_test.jmx") + $planProps + @("-Jsearch=Haupt","-Jdeck_id=1","-Jthink_ms=500")

function Get-CpuSnapshot {
  Get-Process -ErrorAction SilentlyContinue | Group-Object Name | ForEach-Object {
    [pscustomobject]@{ Name = $_.Name; CPU = [double](($_.Group | Measure-Object CPU -Sum).Sum) }
  }
}
function Write-ProcDelta($before, $after, $file) {
  $b = @{}; $before | ForEach-Object { $b[$_.Name] = $_.CPU }
  $after | ForEach-Object { [pscustomobject]@{ Name = $_.Name; CPU_s = [math]::Round($_.CPU - [double]$b[$_.Name], 1) } } |
    Sort-Object CPU_s -Descending | Select-Object -First 8 |
    Format-Table -AutoSize | Out-String | Set-Content $file
}
function Get-PeakBinPower($csv, $bin = 30) {
  $r = Import-Csv $csv
  $prev = [double]$r[0].'CPU_ENERGY (J)'; $t = 0.0; $e = 0.0; $peak = 0.0
  for ($i = 1; $i -lt $r.Count; $i++) {
    $v = [double]$r[$i].'CPU_ENERGY (J)'
    $t += [double]$r[$i].Delta / 1000
    $e += [math]::Max(0, $v - $prev); $prev = $v
    if ($t -ge $bin) { $peak = [math]::Max($peak, $e / $t); $t = 0.0; $e = 0.0 }
  }
  return $peak
}

function Invoke-Measured($name, [string[]]$Command) {
  $accepted = $false
  for ($try = 1; $try -le 3 -and -not $accepted; $try++) {
    Write-Host "$name (attempt $try)"
    Start-Sleep 30
    Remove-Item "$out/$name.jtl" -ErrorAction SilentlyContinue
    $s1 = Get-CpuSnapshot
    & $Eb -i $Interval --summary -o "$out/$name.csv" -- @Command |
      Tee-Object "$out/$name.summary.txt"
    $s2 = Get-CpuSnapshot
    Write-ProcDelta $s1 $s2 "$out/$name.procs.txt"
    $hasSummary = [bool](Select-String -Path "$out/$name.summary.txt" -Pattern "Energy consumption in joules" -Quiet)
    $peak = if ($hasSummary) { Get-PeakBinPower "$out/$name.csv" } else { 0 }
    if ($hasSummary -and $peak -le $MaxBinWatts) {
      $accepted = $true
    } elseif ($hasSummary -and $try -eq 3) {
      $accepted = $true
      Add-Content "$out/quality.txt" "$name accepted after 3 attempts; peak 30 s power $([math]::Round($peak,2)) W exceeds $MaxBinWatts W"
    } else {
      Write-Warning "$name rejected (summary=$hasSummary, peak 30 s power=$([math]::Round($peak,2)) W). Retrying..."
      foreach ($ext in "csv","summary.txt","procs.txt","jtl") {
        if (Test-Path "$out/$name.$ext") { Move-Item "$out/$name.$ext" "$out/rejected/$name.attempt$try.$ext" -Force }
      }
    }
  }
  if (-not $accepted) { throw "$name failed 3 times (no summary)" }
}

Write-Host "Warm-up (not measured)..."
& $Jmeter @jm -l "$out/warmup.jtl" | Out-Null

$idle = @("powershell","-NoProfile","-Command","Start-Sleep $window")
Invoke-Measured "idle_start" $idle
for ($i = 1; $i -le $Runs; $i++) {
  $n = "{0:D2}" -f $i
  Invoke-Measured "run$n" (@($Jmeter) + $jm + @("-l","$out/run$n.jtl"))
}
Invoke-Measured "idle_end" $idle