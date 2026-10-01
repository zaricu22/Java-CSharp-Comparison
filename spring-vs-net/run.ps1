<#
.SYNOPSIS
    Runs the tests of the Spring Boot and/or ASP.NET Core side, or starts one of the apps.

.EXAMPLE
    ./run.ps1                        # tests, both sides
    ./run.ps1 -Side dotnet           # ASP.NET Core tests only
    ./run.ps1 -Mode run -Side spring # start the Spring app on http://localhost:8080
    ./run.ps1 -Mode run -Side dotnet # start the ASP.NET app on http://localhost:5080

.NOTES
    What it doesn't show:
      - Which side is better. Passing tests prove the facts behind a verdict. The verdict itself (for example "@Transactional beats manual transactions") is a judgement, made with the Methodology criteria.
      - Performance. Test time from this script isn't a measurement. The numbers in the READMEs come from the tools/Measure-*.ps1 scripts: median of several runs, peak RAM, startup, throughput.
      - That the code is the best possible. Only that it compiles and behaves as claimed.

    So the evidence order is:
      1. Tests prove the claims automatically.
          Real proof is in the test code.
      2. (Langs only - console) Demos show that both sides produce the same/different results (printed output).
      3. (Frames only - server) Running the app lets you explore by hand, things like:
          Error format (T04), Validation errors (T04), Security (T12),
          Rate limiting (T13), API versioning (T19), Pagination (T17), Health (T14),
          OpenAPI (T16), Output caching (T21), User accounts (T20), Startup log
#>
param(
    # Command line arguments: PowerShell parses and validates them (-Mode test|run, -Side all|spring|dotnet).
    # 1st Parameter
    [ValidateSet('test', 'run')] [string] $Mode = 'test',
    # 2nd Parameter
    [ValidateSet('all', 'spring', 'dotnet')] [string] $Side = 'all'
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

# Validate arguments
if ($Mode -eq 'run' -and $Side -eq 'all') {
    throw 'Choose one app to run: -Side spring or -Side dotnet'
}

# Spring Boot - tests or run the app
if ($Side -in 'all', 'spring') {
    Write-Host "`n########## SPRING BOOT ##########" -ForegroundColor Green
    $env:JAVA_HOME = Find-Jdk
    Push-Location (Join-Path $root 'spring')
    try {
        if ($Mode -eq 'test') {
            mvn -B -q test   # Run tests in quiet mode, no progress bar, no color
            if ($LASTEXITCODE -eq 0) { Write-Host 'Spring tests passed' -ForegroundColor Green }
        } else {
            mvn -B -q spring-boot:run   # Run the Spring Boot app (default port 8080; config in src/main/resources/application.yml)
        }
        if ($LASTEXITCODE -ne 0) { throw "Spring $Mode failed" }
    } finally { Pop-Location }
}

# ASP.NET Core - tests or run the app
if ($Side -in 'all', 'dotnet') {
    Write-Host "`n########## ASP.NET CORE ##########" -ForegroundColor Magenta
    Push-Location (Join-Path $root 'dotnet')
    try {
        if ($Mode -eq 'test') {
            dotnet test --nologo   # Run tests (--nologo hides the .NET SDK banner)
        } else {
            dotnet run --project src/Shop.Api   # Run the ASP.NET Core app (Properties/launchSettings.json: Development environment, port 5080)
        }
        if ($LASTEXITCODE -ne 0) { throw ".NET $Mode failed" }
    } finally { Pop-Location }
}
