<#
.SYNOPSIS
    Counts code lines per topic for both comparison projects - the numbers used in the READMEs.

.DESCRIPTION
    Counted: every line of a topic's main source files.
    Not counted:
      - blank lines
      - comment-only lines (starting with //, /*, *, ///; # in .yml)
      - import / package lines (Java) and using-directives / namespace lines (C#)
        (a C# `using var x = ...` statement IS counted - it is code, not a directive)
      - Demo files (java-vs-c#) and bin/obj/target build output
    Brace-only lines are counted as written (C# puts { on its own line).

.EXAMPLE
    ./tools/Measure-CodeLines.ps1                      # both projects
    ./tools/Measure-CodeLines.ps1 -Project spring-vs-net
#>
param(
    [ValidateSet('all', 'java-vs-c#', 'spring-vs-net')] [string] $Project = 'all'
)

$root = Split-Path $PSScriptRoot -Parent

function Test-CodeLine([string] $line, [string] $extension) {
    $t = $line.Trim()
    if ($t -eq '') { return $false }
    switch ($extension) {
        '.yml' { return -not $t.StartsWith('#') }
        '.json' { return $true }
    }
    if ($t.StartsWith('//') -or $t.StartsWith('/*') -or $t.StartsWith('*')) { return $false }
    if ($extension -eq '.java' -and $t -match '^(import|package)\s') { return $false }
    if ($extension -eq '.cs') {
        if ($t -match '^(global\s+)?using\s+(static\s+)?[\w.]+(\s*=\s*[\w.<>, ]+)?\s*;$') { return $false } # directive, not `using var`
        if ($t -match '^namespace\s') { return $false }
    }
    return $true
}

function Measure-Files([string[]] $paths) {
    $total = 0
    foreach ($path in $paths) {
        if (-not (Test-Path $path)) { continue }
        $files = if ((Get-Item $path).PSIsContainer) {
            Get-ChildItem $path -Recurse -File -Include *.java, *.cs |
                Where-Object { $_.FullName -notmatch '\\(bin|obj|target)\\' -and $_.BaseName -ne 'Demo' }
        } else { Get-Item $path }
        foreach ($file in $files) {
            foreach ($line in [IO.File]::ReadAllLines($file.FullName)) {
                if (Test-CodeLine $line $file.Extension) { $total++ }
            }
        }
    }
    return $total
}

function Show-Project([string] $name, [string] $javaTopics, [string] $netTopics, [hashtable] $extraRows, [string] $leftTitle, [string] $rightTitle) {
    Write-Host "`n=== $name ===" -ForegroundColor Cyan
    $rows = @()
    foreach ($key in $extraRows.Keys | Sort-Object) {
        $rows += [pscustomobject]@{ Topic = $key; $leftTitle = (Measure-Files $extraRows[$key][0]); $rightTitle = (Measure-Files $extraRows[$key][1]) }
    }
    foreach ($dir in Get-ChildItem $javaTopics -Directory | Where-Object Name -match '^t\d\d_' | Sort-Object Name) {
        $number = $dir.Name.Substring(1, 2)
        $net = Get-ChildItem $netTopics -Directory | Where-Object Name -match "^T$($number)_" | Select-Object -First 1
        $rows += [pscustomobject]@{
            Topic = "T$number $($dir.Name.Substring(4))"
            $leftTitle = Measure-Files $dir.FullName
            $rightTitle = if ($net) { Measure-Files $net.FullName } else { 0 }
        }
    }
    $rows | Format-Table -AutoSize | Out-String -Width 120 | Write-Host
}

if ($Project -in 'all', 'java-vs-c#') {
    $j = Join-Path $root 'java-vs-c#'
    Show-Project 'java-vs-c#' "$j\java\src\main\java\shop" "$j\csharp\src\Shop" @{
        '-- domain' = @("$j\java\src\main\java\shop\domain", "$j\csharp\src\Shop\Domain")
        '-- tests (all)' = @("$j\java\src\test", "$j\csharp\tests")
    } 'Java' 'CSharp'
}

if ($Project -in 'all', 'spring-vs-net') {
    $s = Join-Path $root 'spring-vs-net'
    $res = "$s\spring\src\main\resources"
    $api = "$s\dotnet\src\Shop.Api"
    Show-Project 'spring-vs-net' "$s\spring\src\main\java\shop" $api @{
        '-- domain' = @("$s\spring\src\main\java\shop\domain", "$api\Domain")
        '-- entry + config' = @(@("$s\spring\src\main\java\shop\ShopApplication.java", "$res\application.yml", "$res\application-prod.yml"),
                                @("$api\Program.cs", "$api\appsettings.json", "$api\appsettings.Production.json"))
        '-- tests (all)' = @("$s\spring\src\test", "$s\dotnet\tests")
    } 'Spring' 'AspNet'
}
