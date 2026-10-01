<#
.SYNOPSIS
    Runs a command N times and reports the median wall time and median peak RAM of the process.

.DESCRIPTION
    Wall time: from process start to exit (includes runtime startup).
    Peak RAM:  peak working set read from Windows after exit (exact, also for very short runs).
    For wrapper commands (cmd /c mvn ..., dotnet test) only the wall time is meaningful,
    because the measured process is the wrapper, not the JVM/test host it starts.

.EXAMPLE
    ./tools/Measure-Run.ps1 -Exe java -Arguments "-cp target/classes shop.Main" -Dir java-vs-c#/java -Runs 5
    ./tools/Measure-Run.ps1 -Exe dotnet -Arguments "Shop.dll 21" -Dir java-vs-c#/csharp/src/Shop/bin/Release/net10.0
#>
param(
    [Parameter(Mandatory)] [string] $Exe,
    [string] $Arguments = '',
    [string] $Dir = (Get-Location).Path,
    [int] $Runs = 5,
    [string] $Label = "$Exe $Arguments"
)

. "$PSScriptRoot\PeakMemory.ps1"

function Invoke-Once {
    $psi = New-Object System.Diagnostics.ProcessStartInfo $Exe
    $psi.Arguments = $Arguments
    $psi.WorkingDirectory = (Resolve-Path $Dir).Path
    $psi.UseShellExecute = $false
    $psi.RedirectStandardOutput = $true
    $psi.RedirectStandardError = $true
    $watch = [Diagnostics.Stopwatch]::StartNew()
    $process = [Diagnostics.Process]::Start($psi)
    $handle = $process.Handle # keep the handle open so the counters survive the exit
    $null = $process.StandardOutput.ReadToEndAsync()
    $null = $process.StandardError.ReadToEndAsync()
    $process.WaitForExit()
    $watch.Stop()
    [pscustomobject]@{
        Ms = $watch.ElapsedMilliseconds
        PeakMB = [math]::Round([PeakMemory]::PeakWorkingSet($handle) / 1MB)
        Exit = $process.ExitCode
    }
}

$results = @(1..$Runs | ForEach-Object { Invoke-Once })
"{0}: median {1} ms, peak {2} MB   (runs: {3}; exit codes: {4})" -f $Label,
    (Get-Median $results.Ms), (Get-Median $results.PeakMB),
    (($results | ForEach-Object { "$($_.Ms)ms/$($_.PeakMB)MB" }) -join ', '), (($results.Exit) -join ',')
