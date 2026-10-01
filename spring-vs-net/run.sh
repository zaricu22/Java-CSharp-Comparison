#!/usr/bin/env bash
# Runs the tests of the Spring Boot and/or ASP.NET Core side, or starts one of the apps (Bash version of run.ps1).
#
#   ./run.sh                           # tests, both sides
#   ./run.sh --side dotnet             # ASP.NET Core tests only
#   ./run.sh --mode run --side spring  # start the Spring app on http://localhost:8080
#   ./run.sh --mode run --side dotnet  # start the ASP.NET app on http://localhost:5080
#
# What it doesn't show:
#   - Which side is better. Passing tests prove the facts behind a verdict. The verdict itself (for example "@Transactional beats manual transactions") is a judgement, made with the Methodology criteria.
#   - Performance. Test time from this script isn't a measurement. The numbers in the READMEs come from the tools/Measure-*.ps1 scripts: median of several runs, peak RAM, startup, throughput.
#   - That the code is the best possible. Only that it compiles and behaves as claimed.
#
# So the evidence order is:
#   1. Tests prove the claims automatically.
#       Real proof is in the test code.
#   2. (Langs only - console) Demos show that both sides produce the same/different results (printed output).
#   3. (Frames only - server) Running the app lets you explore by hand, things like:
#       Error format (T04), Validation errors (T04), Security (T12), 
#       Rate limiting (T13), API versioning (T19), Pagination (T17), Health (T14), 
#       OpenAPI (T16), Output caching (T21), User accounts (T20), Startup log
set -uo pipefail

mode=test side=all
# Parse command line arguments in pairs (option + value): --mode test|run, --side all|spring|dotnet.
# An unknown option stops the script with exit code 2.
while [ $# -gt 0 ]; do
    case "$1" in
        # Param=Value + shift 2 to get next parameter
        --mode) mode="${2:-}"; shift 2 ;;
        --side) side="${2:-}"; shift 2 ;;
        -h|--help) sed -n '2,7p' "$0"; exit 0 ;;
        *) echo "Unknown option: $1 (see --help)" >&2; exit 2 ;;
    esac
done
# Validate arguments
case "$mode" in test|run) ;; *) echo "--mode must be test or run" >&2; exit 2 ;; esac
case "$side" in all|spring|dotnet) ;; *) echo "--side must be all, spring or dotnet" >&2; exit 2 ;; esac
if [ "$mode" = run ] && [ "$side" = all ]; then
    echo "Choose one app to run: --side spring or --side dotnet" >&2; exit 2
fi

# Finds the folder that run.sh itself is in ($0 - the path the script was started with)
root="$(cd "$(dirname "$0")" && pwd)"

# Turns a Windows path into a Unix-style path when it runs on Windows
to_unix_path() { 
    if command -v cygpath >/dev/null 2>&1; then # is the cygpath tool available?
        cygpath -u "$1";        # yes: convert the path
    else printf '%s\n' "$1";    # no: print it unchanged
    fi; 
}

find_jdk() {
    # Maven uses JAVA_HOME; make sure it points to a JDK 25+.
    # Scripts find the JDK 25 location and override JAVA_HOME for that run only. 
    # Looks in JAVA25_HOME, JAVA_HOME, ~/.jdks (Windows user folder), /usr/lib/jvm (Linux),
    # /Library/Java/JavaVirtualMachines (macOS) and C:\Program Files\Java (Windows).
    local best="" best_version=0 jdk version
    for jdk in "${JAVA25_HOME:-}" "${JAVA_HOME:-}" "$HOME"/.jdks/* /usr/lib/jvm/* \
               /Library/Java/JavaVirtualMachines/*/Contents/Home "/c/Program Files/Java"/jdk-*; do
        [ -n "$jdk" ] || continue
        jdk="$(to_unix_path "$jdk")"
        [ -f "$jdk/release" ] || continue
        version="$(sed -n 's/^JAVA_VERSION="\([0-9]*\).*/\1/p' "$jdk/release")"
        if [ -n "$version" ] && [ "$version" -gt "$best_version" ]; then best="$jdk"; best_version="$version"; fi
    done
    if [ "$best_version" -lt 25 ]; then echo "JDK 25+ not found. Set JAVA25_HOME to its folder." >&2; return 1; fi
    printf '%s\n' "$best"
}

# Spring Boot - tests or run the app
if [ "$side" = all ] || [ "$side" = spring ]; then
    printf '\n\033[32m########## SPRING BOOT ##########\033[0m\n'
    JAVA_HOME="$(find_jdk)" || exit 1
    export JAVA_HOME
    (
        cd "$root/spring" || exit 1
        if [ "$mode" = test ]; then
            mvn -B -q test && printf '\033[32mSpring tests passed\033[0m\n' # Run tests in quiet mode, no progress bar, no color
        else
            mvn -B -q spring-boot:run   # Run the Spring Boot app (default port 8080; config in src/main/resources/application.yml)
        fi
    ) || { echo "Spring $mode failed" >&2; exit 1; }
fi

# ASP.NET Core - tests or run the app
if [ "$side" = all ] || [ "$side" = dotnet ]; then
    printf '\n\033[35m########## ASP.NET CORE ##########\033[0m\n'
    (
        cd "$root/dotnet" || exit 1
        if [ "$mode" = test ]; then
            dotnet test --nologo   # Run tests (--nologo hides the .NET SDK banner)
        else
            dotnet run --project src/Shop.Api   # Run the ASP.NET Core app (Properties/launchSettings.json: Development environment, port 5080)
        fi
    ) || { echo ".NET $mode failed" >&2; exit 1; }
fi
