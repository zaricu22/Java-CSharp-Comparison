# Java vs C# and Spring Boot vs ASP.NET Core

Two side-by-side projects: one compares the **languages** (Java vs C#), the other the **web frameworks** built on them
(Spring Boot vs ASP.NET Core).\
Each builds the same thing twice, with tests on both sides and a verdict comment in every file.

| Folder | What it compares | Topics | Tests |
|--------|------------------|:------:|:-----:|
| [`java-vs-c#`](java-vs-c%23/README.md) | The languages: Java 25 vs C# 14, the same shop domain as a console app | 24 | 104 + 104 |
| [`spring-vs-net`](spring-vs-net/README.md) | The frameworks: Spring Boot 4 vs ASP.NET Core 10, the same shop REST API | 21 | 61 + 60 |

Each folder has its own `run.ps1` (Windows PowerShell) and `run.sh` (Linux, macOS, Git Bash), which run the tests and the demos or the apps, and a README with the layout, the methodology, a verdict table and the measured results.

**Contents:**

- [Measurement scripts](#measurement-scripts)
- [Java 21 → Java 25 migration](#java-21--java-25-migration)
  - [Why the projects moved to Java 25](#why-the-projects-moved-to-java-25)
  - [Observed measurements on both JDKs (with minor code changes)](#observed-measurements-on-both-jdks-with-minor-code-changes)
  - [Why Java 25 is faster (when the syntax is nearly the same)](#why-java-25-is-faster-when-the-syntax-is-nearly-the-same)

## Measurement scripts

The shared measurement scripts live in [`tools/`](tools) and are used by both projects.\
Each project's **Methodology** section explains how verdicts are decided and lists the exact commands.

| Script | Purpose | How it works / what it measures | Used for |
|--------|---------|---------------------------------|----------|
| `Measure-CodeLines.ps1` | Code-line counts per topic | Counts every line of a topic's main source files, skipping blank lines, comment-only lines, Java `import`/`package` lines, C# `using` directives and `namespace` lines, and the `Demo` files. Prints one row per topic, plus domain, entry + config, and all tests. | the "Code lines" columns in both projects (`-Project java-vs-c#` / `spring-vs-net`) |
| `Measure-Run.ps1` | Wall time and peak RAM of a command | Runs the command N times (default 5) with its output hidden. Wall time = process start to exit. Peak RAM = the peak working set, read from Windows (`GetProcessMemoryInfo`) after the process exits, so it is exact even for very short runs. Prints the median and every run. For wrapper commands (`cmd /c mvn`, `dotnet test`) only the time is meaningful. | runs and measures the demos (java-vs-c#), the test suites and the builds (both projects) |
| `Measure-Server.ps1` | Startup, memory and throughput of a web app | Starts the app N times (default 3). Startup = time until the first HTTP `200` on the first endpoint, polled every 20 ms. Idle RAM = working set 0.5 s later. Then, per endpoint, warm-up requests and a measured batch through `loadgen` (req/s, p99 latency, errors), RAM after the load and peak RAM. Stops the app and prints every run and the medians. | Spring and ASP.NET server measurements in spring-vs-net |
| `loadgen/` | Neutral HTTP load generator | A small .NET console app: one pooled `HttpClient` with keep-alive connections sends N requests with C in parallel (default 32) after W warm-up requests, and reports req/s, p50/p99 latency and the number of non-`200` responses. The same client is used for both servers. | called by `Measure-Server.ps1`, or by hand for one endpoint |
| `PeakMemory.ps1` | Shared helper | Reads the exact peak working set of a process through the Windows API, and calculates medians. Loaded by `Measure-Run.ps1` and `Measure-Server.ps1`. | not run directly |

## Java 21 → Java 25 migration

### Why the projects moved to Java 25

Both projects were first built on **Java 21**, the JDK installed on the used machine. That was an unequal pairing: Java 21 is
the LTS from **September 2023**, while C# 14 / .NET 10 is the LTS from **November 2025**.\
For a fair comparison both
sides now use their autumn-2025 LTS releases: **Java 25 (September 2025)** vs **C# 14 / .NET 10 (November 2025)**.
Spring Boot 4 officially supports both Java 21 and 25.

The Java code barely changed:
- unnamed variables `_` in patterns, catch blocks and lambdas (java-vs-c# T08, T09, T10, T12, T14, T18)
- an instance `void main` in `java-vs-c#/java/.../Main.java`
- nothing at all on the Spring side

No verdict changed. All tests pass on both versions.

### Observed measurements on both JDKs (with minor code changes)

Same machine, same settings (no JVM flags), median of 3–5 runs. Details and commands are in each project's Methodology.

| Measurement | Java 21 | Java 25 | Change |
|-------------|:-------:|:-------:|:------:|
| **java-vs-c#**: T21, 1M points (wall time) | 184 ms | 152 ms | **−17%** |
| java-vs-c#: T21 peak RAM | 118 MB | 96 MB | **−19%** |
| java-vs-c#: T21 bytes per `Point` object | 36 | 36 (28 with compact headers) | 0% (−22% opt-in) |
| java-vs-c#: all 24 demos (wall time) | 653 ms | 637 ms | −2% *(noise level)* |
| java-vs-c#: all 24 demos peak RAM | 124 MB | 147 MB | **+19%** |
| java-vs-c#: T01 alone (mostly JVM startup) | 159 ms / 44 MB | 154 ms / 53 MB | −3% / +20% |
| java-vs-c#: test suite (104 tests) | 4.5 s | 4.2 s | −8% |
| java-vs-c#: clean compile | 4.5 s | 3.8 s | −15% |
| Maven's own startup (`mvn validate`) | 1.8 s | 1.6 s | −12% |
| **spring-vs-net**: startup, fat jar | 10.1 s | 8.4 s | **−17%** |
| spring-vs-net: startup with the AOT cache | not available | **3.2 s** | −62% vs fat jar |
| spring-vs-net: RAM after startup | 382 MB | 399 MB | +4% |
| spring-vs-net: test suite (61 tests) | 29.6 s | 26.2 s | −12% |
| spring-vs-net: clean compile / clean package | 5.9 s / 9.2 s | 4.9 s / 7.5 s | −16% / −19% |

For comparison, C# / .NET 10 on the same rows: T21 61 ms / 35 MB / 16 bytes per point, Spring-equivalent startup 2.1 s, test suites 2.5 s and 5.8 s.

### Why Java 25 is faster (when the syntax is nearly the same)

> [!NOTE]
> **The speed doesn't come from the language, it comes from the JVM that runs it.** The Java source above compiles to practically the same bytecode on 21 and 25.
>
> - **Between Java 21 (Sept 2023) and Java 25 (Sept 2025) there were four feature releases.** Each ships a large set of improvements to the HotSpot JIT compilers, the garbage collectors, class loading and the JDK libraries. Unchanged code picks all of them up with no rewrite.
> - **Not isolated here:** the table shows the combined effect. We didn't measure which change contributes what.
>
> **Where Java 25 was worse: peak RAM in the short demo runs** (124 → 147 MB, T01 44 → 53 MB). A likely factor is different default memory ergonomics (heap sizing, the default CDS archive mapped at startup), but the cause wasn't investigated, so treat it as an observation.
>
> **The takeaway:** upgrading the JDK gives a free 10–20% on startup, builds and allocation-heavy code without touching the source. .NET still leads on every row here with default settings, but Java 25's opt-in features (AOT cache, compact headers) close a large part of the startup and memory gap.
>
> **Caveats:** one machine (Ryzen 5 5600H, 15.3 GB RAM, Windows 11), default settings, medians of 3–5 runs. Differences below about 5% (the full demo run, T01 time) are within run-to-run noise.
