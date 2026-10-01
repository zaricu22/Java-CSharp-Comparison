<#
.SYNOPSIS
    Starts a web app N times and measures startup, RAM and throughput.

.DESCRIPTION
    Per run:
      1. start the app; startup = time until the first HTTP 200 on the first endpoint
      2. RAM after startup = working set 0.5 s later (idle, before any load)
      3. for every endpoint: warm-up requests, then the measured batch with tools/loadgen
         (32 concurrent keep-alive connections) -> req/s, p99 latency, errors
      4. RAM after load and peak RAM; stop the app
    Prints every run and the medians.

.EXAMPLE
    ./tools/Measure-Server.ps1 -Label Spring -Exe java -Port 18080 -Dir spring-vs-net/spring `
        -Arguments "-jar target/spring-shop-1.0.0.jar --server.port=18080 --spring.profiles.active=prod"
    ./tools/Measure-Server.ps1 -Label AspNet -Exe dotnet -Port 18081 -Dir spring-vs-net/dotnet/src/Shop.Api/bin/Release/net10.0 `
        -Arguments "Shop.Api.dll" -Environment @{ ASPNETCORE_URLS = "http://localhost:18081"; ASPNETCORE_ENVIRONMENT = "Production" }
#>
param(
    [Parameter(Mandatory)] [string] $Exe,
    [string] $Arguments = '',
    [Parameter(Mandatory)] [string] $Dir,
    [Parameter(Mandatory)] [int] $Port,
    [string] $Label = $Exe,
    [int] $Runs = 3,
    [hashtable] $Environment = @{},
    [string[]] $Endpoints = @('/api/payments', '/api/products/E1', '/api/reports/revenue-by-category'),
    [int] $Requests = 20000,
    [int] $Warmup = 5000,
    [int] $Concurrency = 32
)

. "$PSScriptRoot\PeakMemory.ps1"

$loadgen = Join-Path $PSScriptRoot 'loadgen\bin\Release\net10.0\loadgen.dll'
if (-not (Test-Path $loadgen)) {
    dotnet build (Join-Path $PSScriptRoot 'loadgen') -c Release -v q | Out-Null
}
$base = "http://localhost:$Port"

function Invoke-Load([string] $path) {
    $line = (& dotnet $loadgen "$base$path" $Requests $Concurrency $Warmup 2>$null | Where-Object { $_ -match 'req/s' } | Select-Object -First 1)
    $parts = $line -split '\|'
    [pscustomobject]@{
        Rps = [int]($parts[0] -replace '[^0-9]', '')
        P99 = ($parts[2] -replace 'p99', '').Trim()
        Errors = [int](($parts[3] -replace '[^0-9]', ''))
    }
}

$rows = @()
for ($run = 1; $run -le $Runs; $run++) {
    $psi = New-Object System.Diagnostics.ProcessStartInfo $Exe
    $psi.Arguments = $Arguments
    $psi.WorkingDirectory = (Resolve-Path $Dir).Path
    $psi.UseShellExecute = $false
    $psi.RedirectStandardOutput = $true
    $psi.RedirectStandardError = $true
    foreach ($key in $Environment.Keys) { $psi.EnvironmentVariables[$key] = $Environment[$key] }

    $watch = [Diagnostics.Stopwatch]::StartNew()
    $process = [Diagnostics.Process]::Start($psi)
    $handle = $process.Handle
    $null = $process.StandardOutput.ReadToEndAsync(); $null = $process.StandardError.ReadToEndAsync()
    do {
        Start-Sleep -Milliseconds 20
        $code = cmd /c "curl -s -o NUL -w %{http_code} $base$($Endpoints[0])"
    } while ($code -ne '200' -and -not $process.HasExited -and $watch.Elapsed.TotalSeconds -lt 120)
    if ($code -ne '200') { throw "$Label did not start (exit code $($process.ExitCode))" }
    $row = [ordered]@{ Run = $run; StartupMs = $watch.ElapsedMilliseconds }
    Start-Sleep -Milliseconds 500
    $process.Refresh(); $row.IdleMB = [math]::Round($process.WorkingSet64 / 1MB)

    foreach ($endpoint in $Endpoints) {
        $load = Invoke-Load $endpoint
        $row["$endpoint req/s"] = $load.Rps
        $row["$endpoint p99"] = $load.P99
        $row["$endpoint errors"] = $load.Errors
    }

    $process.Refresh(); $row.AfterLoadMB = [math]::Round($process.WorkingSet64 / 1MB)
    $row.PeakMB = [math]::Round([PeakMemory]::PeakWorkingSet($handle) / 1MB)
    Stop-Process -Id $process.Id -Force; $process.WaitForExit()
    $rows += [pscustomobject]$row
    Start-Sleep -Seconds 2
}

"== $Label"
$rows | Format-List | Out-String -Width 200
"== $Label medians"
$medians = [ordered]@{}
foreach ($name in $rows[0].PSObject.Properties.Name | Where-Object { $_ -ne 'Run' -and $_ -notlike '* p99' }) {
    $medians[$name] = Get-Median ($rows | ForEach-Object { $_.$name })
}
[pscustomobject]$medians | Format-List | Out-String -Width 200
