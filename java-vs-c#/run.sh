#!/usr/bin/env bash
# Runs the demos or the tests of the Java and/or C# side (Bash version of run.ps1).
#
#   ./run.sh                          # tests, both languages
#   ./run.sh --mode demo              # all demos, both languages
#   ./run.sh --mode demo --topic 09   # only topic 09, both languages
#   ./run.sh --lang csharp            # C# tests only
#
# What it doesn't show:
#   - Which side is better. Passing tests prove the facts behind a verdict. The verdict itself (for example "properties beat getters") is a judgement, made with the Methodology criteria.
#   - Performance. Test time from this script isn't a measurement. The numbers in the READMEs come from the tools/Measure-*.ps1 scripts: median of several runs, peak RAM, startup, throughput.
#   - That the code is the best possible. Only that it compiles and behaves as claimed.
#
# So the evidence order is:
#   1. Tests prove the claims automatically.
#       Real proof is in the test code.
#   2. (Langs only - console) Demos show that both sides produce the same/different results (printed output).
#       They make the difference visible (the topic's point):
#           Type system & generics (T03), Operators & value types (T05), Async (T08),
#           Checked exceptions (T14), Enums (T15), Null safety (T18),
#           Iterators & generators (T19), Memory & value types (T21), Resource cleanup (T22)
#   3. (Frames only - server) Running the app lets you explore by hand: see spring-vs-net/run.sh.
set -uo pipefail

mode=test lang=all topic=
# Parse command line arguments in pairs (option + value): --mode test|demo, --lang all|java|csharp, --topic NN.
# An unknown option stops the script with exit code 2.
while [ $# -gt 0 ]; do
    case "$1" in
        # Param=Value + shift 2 to get next parameter
        --mode)  mode="${2:-}";  shift 2 ;;
        --lang)  lang="${2:-}";  shift 2 ;;
        --topic) topic="${2:-}"; shift 2 ;;
        -h|--help) sed -n '2,7p' "$0"; exit 0 ;;
        *) echo "Unknown option: $1 (see --help)" >&2; exit 2 ;;
    esac
done
# Validate arguments
case "$mode" in test|demo) ;; *) echo "--mode must be test or demo" >&2; exit 2 ;; esac
case "$lang" in all|java|csharp) ;; *) echo "--lang must be all, java or csharp" >&2; exit 2 ;; esac

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

# Java - tests or demos
if [ "$lang" = all ] || [ "$lang" = java ]; then
    printf '\n\033[36m########## JAVA ##########\033[0m\n'
    JAVA_HOME="$(find_jdk)" || exit 1
    export JAVA_HOME
    (
        cd "$root/java" || exit 1
        if [ "$mode" = test ]; then
            mvn -B -q test && printf '\033[32mJava tests passed\033[0m\n' # Run tests in quiet mode, no progress bar, no color
        else
            # Compile, then run the demos with the JDK 25 found above (the topic is passed only when one was given)
            mvn -B -q compile && "$JAVA_HOME/bin/java" -cp target/classes shop.Main ${topic:+"$topic"}
        fi
    ) || { echo "Java $mode failed" >&2; exit 1; }
fi

# C# - tests or demos
if [ "$lang" = all ] || [ "$lang" = csharp ]; then
    printf '\n\033[35m########## C# ##########\033[0m\n'
    (
        cd "$root/csharp" || exit 1
        if [ "$mode" = test ]; then
            dotnet test --nologo   # Run tests (--nologo hides the .NET SDK banner)
        else
            dotnet run --project src/Shop -- ${topic:+"$topic"}   # Build and run the demos (everything after -- goes to the app)
        fi
    ) || { echo "C# $mode failed" >&2; exit 1; }
fi
