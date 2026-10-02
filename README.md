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
- [Troubleshooting](#troubleshooting)
- [Further reading](#further-reading)

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

## Troubleshooting

**Toolchain**

| Symptom | Fix |
|---|---|
| `JDK 25+ not found. Set JAVA25_HOME to its folder.` | Install a JDK 25, or point `JAVA25_HOME` at one. The scripts search `JAVA25_HOME`, `JAVA_HOME`, `~/.jdks`, `C:\Program Files\Java`, `/usr/lib/jvm` and `/Library/Java/JavaVirtualMachines`. |
| `mvn` run by hand fails with `release version 25 not supported` | `JAVA_HOME` points to an older JDK; Maven compiles with whatever JDK that is. Set `JAVA_HOME` to the JDK 25 for that shell, or use `run.ps1` / `run.sh`, which do it for one run. |
| `A compatible .NET SDK was not found` | `global.json` asks for SDK 10.0.100 or a later 10.0 feature band. Install the .NET 10 SDK; `dotnet --list-sdks` shows what is installed. |
| Maven or NuGet can't download packages: `Permission denied: getsockopt`, `WSAEACCES`, or `PKIX path building failed` | A firewall or antivirus blocks outbound connections from `java.exe` / `dotnet.exe`, or intercepts HTTPS. Allow both programs, or download the dependencies with a tool that isn't blocked: into `~/.m2` for Maven (then `mvn -o`), and into a local folder for NuGet (then `dotnet restore --source <folder>`). This happened on the machine the project was built on. |
| `run.ps1 cannot be loaded because running scripts is disabled` | `powershell -ExecutionPolicy Bypass -File .\run.ps1 ...`, or `Set-ExecutionPolicy -Scope CurrentUser RemoteSigned` once. |
| `run.sh`: `Permission denied`, or `/usr/bin/env: 'bash\r'` | Start it with `bash run.sh ...`, or `chmod +x run.sh`. The `\r` error means the file was checked out with Windows line endings: convert it to LF (`dos2unix run.sh`, or `git config core.autocrlf input` and check out again). |

**Running the apps (spring-vs-net)**

| Symptom | Fix |
|---|---|
| `Choose one app to run: -Side spring or -Side dotnet` | `-Mode run` starts one server at a time. Pass `-Side spring` or `-Side dotnet` (`--side` in `run.sh`). |
| `Port 8080 was already in use` (Spring), or `address already in use` on 5080 (ASP.NET) | Stop the other process, or pick another port: `mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081`, `dotnet run --project src/Shop.Api --urls http://localhost:5081`. |
| ASP.NET app refuses to start with a configuration validation error, or uses the wrong (SMTP) mail sender | It reads `appsettings.json` from the *current directory*, and without `launchSettings.json` it runs as `Production`. Start it from `spring-vs-net/dotnet` with `dotnet run --project src/Shop.Api`, or from the build output folder for `dotnet Shop.Api.dll`. |
| `401` on `/api/admin/...` | The admin endpoints use HTTP Basic: `curl -u admin:admin-pass ...`. |
| Spring tests take noticeably longer than the ASP.NET ones | Expected: tests that change data use `@DirtiesContext`, and each one starts a new Spring context (~1 s). |

**Measurement scripts (`tools/`)**

| Symptom | Fix |
|---|---|
| The `Measure-*.ps1` scripts fail on Linux or macOS | They read peak memory through the Windows API (`GetProcessMemoryInfo`), so they only run on Windows. The tests and demos run everywhere. |
| `Measure-Server.ps1`: `<Label> did not start (exit code N)` | Build the deployable first (`mvn -q package -DskipTests`, `dotnet build src/Shop.Api -c Release`), check that `-Dir` and the port are right, and that nothing else uses the port. |
| `mvn -o ...` in a measurement command fails with an artifact that "has not been downloaded" | Offline mode only uses `~/.m2`. Run one normal build (`mvn -q package`) first. |
| Numbers vary a lot between runs, or a run gets killed | Close other programs: low free RAM and background load distort the medians. Differences below about 5% are noise anyway. |

---

## Further reading

- Java: [JDK 25](https://openjdk.org/projects/jdk/25/) · [JEP 512: instance main methods](https://openjdk.org/jeps/512) · [JEP 456: unnamed variables](https://openjdk.org/jeps/456) · [JEP 444: virtual threads](https://openjdk.org/jeps/444) · [JEP 483: AOT class loading](https://openjdk.org/jeps/483) · [JEP 519: compact object headers](https://openjdk.org/jeps/519) · [Java Language Specification](https://docs.oracle.com/javase/specs/)
- C# and .NET: [What's new in C# 14](https://learn.microsoft.com/en-us/dotnet/csharp/whats-new/csharp-14) · [What's new in .NET 10](https://learn.microsoft.com/en-us/dotnet/core/whats-new/dotnet-10/overview) · [C# language reference](https://learn.microsoft.com/en-us/dotnet/csharp/language-reference/) · [`global.json`](https://learn.microsoft.com/en-us/dotnet/core/tools/global-json)
- Spring: [Spring Boot reference](https://docs.spring.io/spring-boot/reference/) · [Spring Framework reference](https://docs.spring.io/spring-framework/reference/) · [Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/) · [Spring Security](https://docs.spring.io/spring-security/reference/) · [Efficient deployments (extracted jar, AOT cache)](https://docs.spring.io/spring-boot/reference/packaging/efficient.html)
- ASP.NET Core: [Documentation](https://learn.microsoft.com/en-us/aspnet/core/) · [What's new in ASP.NET Core 10](https://learn.microsoft.com/en-us/aspnet/core/release-notes/aspnetcore-10.0) · [EF Core](https://learn.microsoft.com/en-us/ef/core/) · [Output caching](https://learn.microsoft.com/en-us/aspnet/core/performance/caching/output) · [Rate limiting](https://learn.microsoft.com/en-us/aspnet/core/performance/rate-limit)
- Testing: [JUnit user guide](https://docs.junit.org/current/user-guide/) · [xUnit.net](https://xunit.net/)
- Benchmarking (for anything finer than the whole-process measurements here): [JMH](https://github.com/openjdk/jmh) · [BenchmarkDotNet](https://benchmarkdotnet.org/) · [TechEmpower Framework Benchmarks](https://www.techempower.com/benchmarks/)
