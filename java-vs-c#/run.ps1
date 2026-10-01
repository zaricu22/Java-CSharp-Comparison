<#
.SYNOPSIS
    Runs the demos or the tests of the Java and/or C# side.

.EXAMPLE
    ./run.ps1                         # tests, both languages
    ./run.ps1 -Mode demo              # all demos, both languages
    ./run.ps1 -Mode demo -Topic 09    # only topic 09, both languages
    ./run.ps1 -Lang csharp            # C# tests only

.NOTES
    What it doesn't show:
      - Which side is better. Passing tests prove the facts behind a verdict. The verdict itself (for example "properties beat getters") is a judgement, made with the Methodology criteria.
      - Performance. Test time from this script isn't a measurement. The numbers in the READMEs come from the tools/Measure-*.ps1 scripts: median of several runs, peak RAM, startup, throughput.
      - That the code is the best possible. Only that it compiles and behaves as claimed.

    So the evidence order is:
      1. Tests prove the claims automatically.
          Real proof is in the test code.
      2. (Langs only - console) Demos show that both sides produce the same/different results (printed output).
          They make the difference visible (the topic's point):
              Type system & generics (T03), Operators & value types (T05), Async (T08),
              Checked exceptions (T14), Enums (T15), Null safety (T18),
              Iterators & generators (T19), Memory & value types (T21), Resource cleanup (T22)
      3. (Frames only - server) Running the app lets you explore by hand: see spring-vs-net/run.ps1.
#>
param(
    # Command line arguments: PowerShell parses and validates them (-Mode test|demo, -Lang all|java|csharp, -Topic NN).
    # 1st Parameter
    [ValidateSet('test', 'demo')] [string] $Mode = 'test',
    # 2nd Parameter
    [ValidateSet('all', 'java', 'csharp')] [string] $Lang = 'all',
    # 3rd Parameter
    [string] $Topic = ''
)

# 'Continue': Windows PowerShell 5.1 turns any stderr line of a native tool (e.g. a JVM warning) into an error under 'Stop'.
# Failures are detected through $LASTEXITCODE instead.
$ErrorActionPreference = 'Continue'

# Finds the folder that run.ps1 itself is in
$root = $PSScriptRoot

function Find-Jdk {
    # Maven uses JAVA_HOME; make sure it points to a JDK 25+.
    # Scripts find the JDK 25 location and override JAVA_HOME for that run only.
    # Looks in JAVA25_HOME, JAVA_HOME, C:\Program Files\Java\jdk-* and ~\.jdks\jdk-* (Windows user folder).
    $candidates = @($env:JAVA25_HOME, $env:JAVA_HOME) +
        (Get-ChildItem 'C:\Program Files\Java', (Join-Path $HOME '.jdks') -Directory -Filter 'jdk-*' -ErrorAction SilentlyContinue |
            ForEach-Object FullName)
    $best = $null; $bestVersion = 0
    foreach ($jdk in $candidates | Where-Object { $_ }) {
        $release = Join-Path $jdk 'release'
        if ((Test-Path $release) -and ((Get-Content $release -Raw) -match 'JAVA_VERSION="(\d+)') -and [int]$Matches[1] -gt $bestVersion) {
            $best = $jdk; $bestVersion = [int]$Matches[1]
        }
    }
    if ($bestVersion -lt 25) { throw 'JDK 25+ not found. Set JAVA25_HOME to its folder.' }
    return $best
}

# Java - tests or demos
if ($Lang -in 'all', 'java') {
    Write-Host "`n########## JAVA ##########" -ForegroundColor Cyan
    $env:JAVA_HOME = Find-Jdk
    Push-Location (Join-Path $root 'java')
    try {
        if ($Mode -eq 'test') {
            mvn -B -q test   # Run tests in quiet mode, no progress bar, no color
            if ($LASTEXITCODE -eq 0) { Write-Host 'Java tests passed' -ForegroundColor Green }
        } else {
            # Compile, then run the demos with the JDK 25 found above (an empty topic means all topics)
            mvn -B -q compile
            & (Join-Path $env:JAVA_HOME 'bin\java') -cp target/classes shop.Main $Topic
        }
        if ($LASTEXITCODE -ne 0) { throw "Java $Mode failed" }
    } finally { Pop-Location }
}

# C# - tests or demos
if ($Lang -in 'all', 'csharp') {
    Write-Host "`n########## C# ##########" -ForegroundColor Magenta
    Push-Location (Join-Path $root 'csharp')
    try {
        if ($Mode -eq 'test') {
            dotnet test --nologo   # Run tests (--nologo hides the .NET SDK banner)
        } else {
            dotnet run --project src/Shop -- $Topic   # Build and run the demos (everything after -- goes to the app)
        }
        if ($LASTEXITCODE -ne 0) { throw "C# $Mode failed" }
    } finally { Pop-Location }
}
