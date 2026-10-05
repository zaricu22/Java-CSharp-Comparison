# Java vs C# — one app, two languages

This project is based on research into many published articles that compare Java and C#. The authors'
conclusions from those articles were merged into one set of notes, and the aim of this project is to back each
conclusion with working code: wherever a claim can be shown in code,
it becomes a small runnable sample, with tests on both sides, so it can be checked rather than taken on trust.
Where a conclusion turned out to be outdated, the samples show the current state instead.

The same small **shop application** (products, customers, orders, payments, shipping) is written twice,
in **Java 25** and **C# 14 / .NET 10**. Both versions use the same methods, the same sample data and the
same test cases. Each language is written the way an experienced developer would write it today, so the
differences you see come from the languages themselves and not from a weak implementation.

It has 24 topics. Every topic is a package (Java) or namespace (C#) with the same number, a runnable `Demo`
and a test class whose test names match in both languages.

Every `.java` and `.cs` file (sources, demos and tests) starts with the same two-line verdict for its topic:

```java
// VERDICT | T15 Enums | BETTER: JAVA
// WHY: enum constants carry fields, own method bodies and implement interfaces; C# enums are plain ints ((Enum)42 is legal).
```

**Contents:**

- [Running it](#running-it)
- [Project layout](#project-layout)
- [Methodology](#methodology)
  - [How the winner of a topic is decided](#how-the-winner-of-a-topic-is-decided)
  - [Versions compared](#versions-compared)
  - [How "code lines" are counted](#how-code-lines-are-counted)
  - [Performance and other measured aspects: setup](#performance-and-other-measured-aspects-setup)
  - [Other aspects](#other-aspects)
- [Results: which language is better and why (by topic)](#results-which-language-is-better-and-why-by-topic)
  - [Performance: measured results](#performance-measured-results)
- [When to choose which](#when-to-choose-which)
- [Limitations](#limitations)
- [Source notes: Covered by samples](#source-notes-covered-by-samples)
- [Traps the tests show in both languages](#traps-the-tests-show-in-both-languages)
  - [All traps by topic](#all-traps-by-topic)
- [Source notes: Not covered by samples](#source-notes-not-covered-by-samples)
  - [Language & runtime features](#language--runtime-features)
  - [Platform, tooling & ecosystem](#platform-tooling--ecosystem)

## Running it

Requirements: **JDK 25+** with **Maven 3.9+**, and the **.NET 10 SDK**.

> [!NOTE]
> **What running the scripts doesn't show:**
> - **Which side is better.** Passing tests prove the facts behind a verdict. The verdict itself (for example "properties beat getters") is a judgement, made with the [Methodology](#how-the-winner-of-a-topic-is-decided) criteria.
> - **Performance.** Test time from these scripts isn't a measurement. The numbers come from the `tools/Measure-*.ps1` scripts: median of several runs, peak RAM, startup, throughput (see [Performance: measured results](#performance-measured-results)).
> - **That the code is the best possible.** Only that it compiles and behaves as claimed.

**So the evidence order is:**
1. **Tests** prove the claims automatically. Real proof is in the test code.
2. **Demos** (languages only, console) show that both sides produce the same or different results (printed output).
   They make the difference visible (the topic's point): Type system & generics (T03), Operators & value types (T05),
   Async (T08), Checked exceptions (T14), Enums (T15), Null safety (T18), Iterators & generators (T19),
   Memory & value types (T21), Resource cleanup (T22).
3. **Running the app** (frameworks only, server) lets you explore by hand: see [spring-vs-net](../spring-vs-net/README.md#running-it).

```powershell
./run.ps1                          # all tests, Java + C#
./run.ps1 -Mode demo               # all demos, Java + C#
./run.ps1 -Mode demo -Topic 08     # only topic 08, in both languages, one after the other
./run.ps1 -Lang java               # Java only (or -Lang csharp)
```

On Linux, macOS or Git Bash, `run.sh` does the same:

```bash
./run.sh                           # all tests, Java + C#
./run.sh --mode demo               # all demos, Java + C#
./run.sh --mode demo --topic 08    # only topic 08, in both languages, one after the other
./run.sh --lang java               # Java only (or --lang csharp)
```

You can also run each side directly:

```powershell
cd java;   mvn test;   mvn compile; java -cp target/classes shop.Main 04
cd csharp; dotnet test; dotnet run --project src/Shop -- 04
```

`run.ps1` and `run.sh` find a JDK 25+ by themselves, because Maven uses `JAVA_HOME` and that often points to an older JDK. `run.ps1` looks in `C:\Program Files\Java` and `~\.jdks`; `run.sh` also looks in `/usr/lib/jvm` (Linux) and `/Library/Java/JavaVirtualMachines` (macOS).
Set `JAVA25_HOME` to choose a specific JDK.

## Project layout

Both sides have the same types and methods with the same names; only the casing follows each language's convention
(`priceAsync` vs `PriceAsync`). Where the types differ (T04, T06, T08, T10, T13, T15, T16, T20, T21), the difference
**is** what the topic demonstrates. The Java side has **89** files under `src/main` and the C# side has **54** under
`src/Shop`: C# allows several public types per file, while Java needs one file per public type.

```
java-vs-c#/
├── README.md
├── run.ps1                               # runs tests or demos for one or both languages
├── run.sh                                # the same for Linux, macOS and Git Bash
│
├── java/                                 # Java 25 · Maven · JUnit 6
│   ├── pom.xml
│   └── src/
│       ├── main/java/shop/
│       │   ├── Main.java                 # runs every Demo (or one: `shop.Main 04`)
│       │   ├── domain/                   # Category, Product, Customer, OrderLine, Order, SampleData
│       │   ├── t01_properties/           # CustomerAccount, Pricing
│       │   ├── t02_strings/              # InvoiceFormatter
│       │   ├── t03_types/                # TypeSystem, Repository<T>, CartDraft
│       │   ├── t04_queries/              # SalesReport (+ records CustomerTotal, ProductQuantity)
│       │   ├── t05_operators/            # Money, Checkout, PacketReader
│       │   ├── t06_extensions/           # StringUtils, OrderUtils
│       │   ├── t07_events/               # Inventory (+ StockLow), ProductFilters
│       │   ├── t08_async/                # RemoteApis, ProductView, CatalogFutures, CatalogVirtualThreads
│       │   ├── t09_patterns/             # PaymentResult (sealed: Approved, Declined, Pending, FraudSuspected), Patterns
│       │   ├── t10_parameters/           # Parameters (+ record PriceRange), QuoteOptions
│       │   ├── t11_indexers/             # Catalog, ShippingMatrix, Ranges
│       │   ├── t12_errors/               # PaymentClient, GatewayException, ConnectionLostException, GatewayTimeoutException, StockMath
│       │   ├── t13_compiletime/          # SkuValidator, ProductForm + base class ProductFormGenerated, BuildInfo, DynamicSettings
│       │   ├── t14_checked/              # CardProcessor (+ Receipt), FakeCardProcessor, CheckoutService, PaymentDeclinedException
│       │   ├── t15_enums/                # ShippingMethod (enum with bodies), PricingRule, ShippingReport
│       │   ├── t16_innerclasses/         # ShoppingCart (inner Item), DiscountRule, Discounts (anonymous class)
│       │   ├── t17_variance/             # Sellable (Book, Ebook), Variance
│       │   ├── t18_nullsafety/           # CustomerProfile (+ Address), NullSafety
│       │   ├── t19_iterators/            # OrderPages, Iterators (hand-written Iterator)
│       │   ├── t20_expressions/          # Criteria (Field, Condition), Queries
│       │   ├── t21_memory/               # Memory (+ record Point, Points = parallel arrays)
│       │   ├── t22_resources/            # ExportFile, Exporter
│       │   ├── t23_collections/          # CollectionLiterals
│       │   └── t24_loops/                # LoopControl
│       │                                 # (+ Demo.java in every topic)
│       └── test/java/shop/
│           └── t01_properties/ … t24_loops/  # one <Topic>Test.java per topic
│
└── csharp/                               # C# 14 · .NET 10 · xUnit
    ├── JavaVsCSharp.slnx
    ├── global.json                       # pins SDK 10.x
    ├── Directory.Build.props             # net10.0, nullable (as errors), implicit usings, invariant culture
    ├── src/Shop/
    │   ├── Shop.csproj
    │   ├── Program.cs                    # runs every Demo (or one: `dotnet run -- 04`)
    │   ├── Domain/                       # Domain.cs (the same 6 types in one file), SampleData.cs
    │   ├── T01_Properties/               # CustomerAccount.cs: CustomerAccount, Pricing
    │   ├── T02_Strings/                  # InvoiceFormatter.cs: InvoiceFormatter
    │   ├── T03_Types/                    # TypeSystem.cs: TypeSystem, Repository<T>, CartDraft
    │   ├── T04_Queries/                  # SalesReport.cs: SalesReport (named tuples, no helper records)
    │   ├── T05_Operators/                # Money.cs: Money (struct), Checkout, PacketReader
    │   ├── T06_Extensions/               # Extensions.cs: StringExtensions, OrderExtensions
    │   ├── T07_Events/                   # Inventory.cs: Inventory (+ StockLow), ProductFilters
    │   ├── T08_Async/                    # CatalogService.cs: RemoteApis, ProductView, CatalogService
    │   ├── T09_Patterns/                 # Patterns.cs: PaymentResult (Approved, Declined, Pending, FraudSuspected), Patterns
    │   ├── T10_Parameters/               # Parameters.cs: Parameters (optional/named arguments, tuple)
    │   ├── T11_Indexers/                 # Indexers.cs: Catalog, ShippingMatrix, Ranges
    │   ├── T12_Errors/                   # Errors.cs: PaymentClient, GatewayException, ConnectionLostException, GatewayTimeoutException, StockMath
    │   ├── T13_CompileTime/              # SkuValidator.cs, ProductForm.cs + ProductForm.Generated.cs (one partial class), BuildInfo.cs (+ DynamicSettings)
    │   ├── T14_Checked/                  # Checkout.cs: ICardProcessor (+ Receipt), FakeCardProcessor, CheckoutService, PaymentDeclinedException
    │   ├── T15_Enums/                    # ShippingMethod.cs: ShippingMethod (plain enum) + ShippingMethodExtensions, IPricingRule, adapter ShippingRule, ShippingReport
    │   ├── T16_InnerClasses/             # Cart.cs: ShoppingCart (nested Item), IDiscountRule, Discounts + named OneTimePercentRule
    │   ├── T17_Variance/                 # Variance.cs: ISellable (Book, Ebook), Variance
    │   ├── T18_NullSafety/               # NullSafety.cs: CustomerProfile, Address, NullSafety
    │   ├── T19_Iterators/                # Iterators.cs: OrderPages, Iterators (yield return)
    │   ├── T20_Expressions/              # SqlTranslator.cs: SqlTranslator (reads the lambda as an expression tree)
    │   ├── T21_Memory/                   # Memory.cs: Memory (+ struct Point)
    │   ├── T22_Resources/                # Exporter.cs: ExportFile, Exporter
    │   ├── T23_Collections/              # CollectionLiterals.cs: CollectionLiterals
    │   └── T24_Loops/                    # LoopControl.cs: LoopControl
    │                                     # (+ Demo.cs in every topic)
    └── tests/Shop.Tests/
        ├── Shop.Tests.csproj
        └── T01_Properties/ … T24_Loops/  # one <Topic>Tests.cs per topic, same test names as Java
```

## Methodology

### How the winner of a topic is decided

Each topic gets **Java**, **C#** or **Tie**. The criteria, in order of weight:

1. **Capability built into the language.** Can it do the thing directly, or only by a workaround (a helper class, a library, a design pattern, hand-written boilerplate)? Examples: C# properties vs getter/setter pairs (T01), Java enum bodies vs three C# switches (T15). A direct implementation wins over a workaround.
2. **Safety.** Does the compiler prevent the mistake, or does it only show up at runtime? Examples: checked exceptions (T14), nullable reference types (T18), exhaustive sealed switches (T09). The language whose compiler rejects the mistake, and so prevents the exception, wins.
3. **Code needed for the same behaviour, and how readable it is.** Code lines are supporting evidence, not the decider. A shorter version that hides a trap doesn't win.
4. **Proof by tests.** Every in-file claim on which side is better is exercised by a test on both sides, including each language's traps (see [Traps](#traps-the-tests-show-in-both-languages)).

A topic is a **Tie** when both sides are comparably good, or when each side clearly wins a different part of the topic (T07, T08, T09, T22).\
Performance, ecosystem and popularity are **not** used for topic verdicts; they are reported separately below and in the "not covered" tables.\
The verdicts are a judgement, written down with a reason (the `WHY:` line in every file), so you can check the code and disagree.

### Versions compared

| | Java side | C# side |
|---|---|---|
| Language | **Java 25** (LTS, Sept 2025) | **C# 14** (Nov 2025) |
| Runtime | Oracle JDK 25.0.4.1 (HotSpot) | .NET 10.0.12 (LTS, Nov 2025), SDK 10.0.401 |
| Build / test | Maven 3.9.16, JUnit 6.0.2 | MSBuild (.NET SDK), xUnit 2.9.3 |

Both sides are the current long-term-support releases from autumn 2025, so the pairing is equal in age.

> [!NOTE]
> **Java version migration:**
> - The project started on Java 21 and was moved to Java 25. The Java 21 measurements are kept in
>   [Performance: measured results](#performance-measured-results) for reference.
> - **What moving to Java 25 changed in the code** (verdicts unchanged):
>   - **Unnamed variables `_`** (final since Java 22):
>     - T09: `case Approved(var tx, _)`, `case FraudSuspected _`
>     - catch blocks that ignore the exception (T08, T10, T14, T18)
>     - lambdas that ignore their parameter (T12 `_ -> { throw … }`, like C#'s `_ =>`)
>   - **Instance `main` method** (JEP 512, final in Java 25): `Main.java` declares `void main(String[] args)`, with no `public static`.
> - **Java 25 features not used, and why:**
>   - **Stream gatherers (T04):** no report in the sample needs a custom stream step.
>   - **Flexible constructor bodies:** no sample has constructor chains where they would help.
>   - **Module imports:** imports aren't counted anyway.
>   - **Structured concurrency, primitive types in patterns:** still preview features in Java 25.

### How "code lines" are counted

The numbers come from the script [`tools/Measure-CodeLines.ps1`](../tools/Measure-CodeLines.ps1), which lives at the repository root and is shared by both projects:

```powershell
./tools/Measure-CodeLines.ps1 -Project java-vs-c#
```

- **Counted:** every line of a topic's main source files (`java/src/main/java/shop/tNN_*` vs `csharp/src/Shop/TNN_*`).
- **Not counted:**
  - blank lines
  - comment-only lines (`//`, `/*`, `*`, `///`)
  - Java `import`/`package` lines, and C# `using` *directives* and `namespace` lines. A C# `using var x = …` statement is code and **is** counted.
  - the `Demo` files, and build output
- **Identical samples:** both sides implement the same methods with the same behaviour. Helper code such as fake services (`RemoteApis` in T08, `OrderPages` in T19, `ExportFile` in T22) exists on both sides and is counted on both. Where it differs in size, that's a real language difference too. For example, Java's `Thread.sleep` forces a `try/catch` for the checked `InterruptedException`.
- **Brace style is kept as written.** C# puts `{` on its own line (Allman), Java doesn't. This makes C# look a little longer; in T01 the ratio is about 3:1 with or without brace-only lines (54:19 vs 39:13).
- **One deliberate asymmetry:** in T08 the Java count covers two implementations (CompletableFuture *and* virtual threads) against one C# implementation. The table marks it with \*.
- **Tests are not counted** in the topic rows (all tests: 735 Java vs 843 C# lines).

### Performance and other measured aspects: setup

The results are in [Performance: measured results](#performance-measured-results), under the topic table.

- **Machine:** AMD Ryzen 5 5600H (6 cores / 12 threads), 15.3 GB RAM, Windows 11.
- **Java:** `java -cp target/classes shop.Main` on JDK 25, with no JVM flags (default G1 GC and heap sizing). T21 is also measured with `-XX:+UseCompactObjectHeaders`.
- **C#:** `dotnet Shop.dll`, a Release, framework-dependent build with default settings.
- **Timing:** wall time from process start to exit, including runtime startup.
- **Memory:** peak working set (peak RAM) read from Windows after the process exits.
- **Runs:** median of 5 runs for demos, and of 3 runs for tests and builds.

**Running performance measures (reproducible):**

All scripts are in [`tools/`](../tools). `Measure-Run.ps1` runs a command N times and prints the median wall time and peak RAM. Peak RAM is exact because it is read from Windows via `GetProcessMemoryInfo` after exit; for wrapper commands (`cmd /c mvn`, `dotnet test`) only the time is meaningful.

```powershell
# [setup] build first (JAVA_HOME = a JDK 25)
cd java-vs-c#/java;   mvn -q compile
cd java-vs-c#/csharp; dotnet build src/Shop -c Release

# [performance - runtime startup + memory] topic 01 alone is mostly runtime startup (wall time, peak RAM)
./tools/Measure-Run.ps1 -Exe java   -Arguments "-cp target/classes shop.Main 01" -Dir java-vs-c#/java -Runs 5
./tools/Measure-Run.ps1 -Exe dotnet -Arguments "Shop.dll 01"                     -Dir java-vs-c#/csharp/src/Shop/bin/Release/net10.0 -Runs 5

# [performance - wall time + memory] all 24 demos
./tools/Measure-Run.ps1 -Exe java   -Arguments "-cp target/classes shop.Main"    -Dir java-vs-c#/java -Runs 5
./tools/Measure-Run.ps1 -Exe dotnet -Arguments "Shop.dll"                        -Dir java-vs-c#/csharp/src/Shop/bin/Release/net10.0 -Runs 5

# [performance - wall time + memory] topic 21 (1M points), topic 08 (includes the fake remote-call waits)
./tools/Measure-Run.ps1 -Exe java   -Arguments "-cp target/classes shop.Main 21" -Dir java-vs-c#/java -Runs 5
./tools/Measure-Run.ps1 -Exe dotnet -Arguments "Shop.dll 21"                     -Dir java-vs-c#/csharp/src/Shop/bin/Release/net10.0 -Runs 5
./tools/Measure-Run.ps1 -Exe java   -Arguments "-cp target/classes shop.Main 08" -Dir java-vs-c#/java -Runs 5
./tools/Measure-Run.ps1 -Exe dotnet -Arguments "Shop.dll 08"                     -Dir java-vs-c#/csharp/src/Shop/bin/Release/net10.0 -Runs 5

# [performance - memory] topic 21 with compact object headers (Java 25)
./tools/Measure-Run.ps1 -Exe java   -Arguments "-XX:+UseCompactObjectHeaders -cp target/classes shop.Main 21" -Dir java-vs-c#/java -Runs 5

# [developer loop - test time] no rebuild
./tools/Measure-Run.ps1 -Exe cmd    -Arguments "/c mvn -o -q -B surefire:test"   -Dir java-vs-c#/java -Runs 3
./tools/Measure-Run.ps1 -Exe dotnet -Arguments "test --no-build --nologo"        -Dir java-vs-c#/csharp -Runs 3

# [developer loop - build time] clean builds
./tools/Measure-Run.ps1 -Exe cmd    -Arguments "/c mvn -o -q -B clean compile"   -Dir java-vs-c#/java -Runs 3
./tools/Measure-Run.ps1 -Exe dotnet -Arguments "build src/Shop -c Release --no-incremental --no-restore --disable-build-servers" -Dir java-vs-c#/csharp -Runs 3

# [developer loop - test + build time] Maven's own startup (part of both)
./tools/Measure-Run.ps1 -Exe cmd    -Arguments "/c mvn -o -q -B validate"        -Dir java-vs-c#/java -Runs 3

# [performance - memory] bytes per point (T21): printed by the demo itself (Java: ThreadMXBean.getThreadAllocatedBytes, .NET: GC.GetAllocatedBytesForCurrentThread)
java -cp target/classes shop.Main 21
```

`--disable-build-servers` makes .NET build without a warm background compiler, which matches Maven (no daemon).

### Other aspects

> [!NOTE]
> - **Measured above:** runtime startup, memory, test time, build time.
> - **Measured in `spring-vs-net`:** deployable size and number of libraries, server startup, memory and throughput.
> - **Not measured, but discussed in [Platform, tooling & ecosystem](#platform-tooling--ecosystem):** IDE and tooling experience, library ecosystem, community, job market, platform reach (Android, games, desktop). These aren't reproducible on one machine, so they're reported from current, public facts.

## Results: which language is better and why (by topic)

How "code lines" are counted, and which versions are compared: see [Methodology](#methodology).

**Tally: C# 15 · Java 5 · tie 4.**\
C# wins mostly on syntax and expressiveness.\
Java wins on compile-time safety and cohesion: checked exceptions, sealed exhaustiveness, rich enums, and no invalid enum values.

> [!NOTE]
> The table uses these marks:
>
> - *(note outdated)* in the Better column: the source notes named C# as better, but Java has caught up since the articles were written, so it is now a tie. All other verdicts agree with the notes, or the topic isn't in the notes (T18, T19, T22).
> - `*` in the code lines column: Java's T08 count includes two implementations (CompletableFuture and virtual threads).
> - `**` in the code lines column: C#'s T19 count includes both a synchronous and an async (`IAsyncEnumerable`) version.

| T#  | Topic | Better | Why (concise) | Code lines Java / C# |
|-----|-------|:------:|---------------|:-----:|
| T01 | Properties & records | **C#** | Properties (`field`, `required`, `init`) and `with` replace hand-written getters/setters and manual record copies. | 54 / 19 |
| T02 | Strings | **C#** | `$"{x,-12} {y:F2}"` puts each value where it's printed. Java only has positional `%s` (String Templates were withdrawn). | 30 / 16 |
| T03 | Type system & generics | **C#** | Reified generics: `typeof(T)`, `new T()`, `List<int>` without boxing, generic math. Java has erasure and `Integer ==` traps. | 52 / 32 |
| T04 | LINQ vs Streams | **C#** | `GroupBy/Sum/MaxBy`, tuples, query syntax. Java goes through `Collectors.groupingBy(…, reducing(…))` and helper records. | 52 / 31 |
| T05 | Operators & value types | **C#** | `(price * qty + ship) * 0.9m` with structs, `decimal` and unsigned `byte`. Java writes `.times().plus().times()` and masks bytes with `& 0xFF`. | 63 / 40 |
| T06 | Extension methods | **C#** | `orders.PlacedIn(…).From(…).Revenue` reads left to right. Java nests static `Utils` calls inside out. | 23 / 16 |
| T07 | Events & functions | **Tie** *(note outdated)* | C#: `event` with `+=`/`-=` (only the owner can raise it), and closures can modify locals. Java: built-in composition (`and/negate/andThen`), and effectively-final capture blocks the shared loop-variable bug. | 41 / 28 |
| T08 | Async | **Tie** *(note outdated)* | C# `async/await` is much better than `CompletableFuture`. Java 21+ virtual threads allow plain blocking code with no async "coloring". | 85* / 39 |
| T09 | Pattern matching | **Tie** *(note outdated)* | C# has relational, tuple and list patterns. Java checks exhaustiveness over `sealed` types at compile time. | 40 / 37 |
| T10 | Method parameters | **C#** | Optional/named arguments, `out`/`ref`, tuple returns. Java needs parameter objects, result records and try/catch around parsing. | 49 / 24 |
| T11 | Indexers & ranges | **C#** | `catalog["E1"]`, `matrix[1, 1]`, `items[1..^1]`. Java spells these as `get()` / `subList()` / `substring()`. | 47 / 32 |
| T12 | Exception filters & overflow | **C#** | `catch … when` and `checked { }` blocks. Java catches, inspects and rethrows, and needs `Math.*Exact` per operation. *Java wins one part:* multi-catch `catch (A \| B e)`; C# needs `catch (Exception e) when (e is A or B)`. | 72 / 68 |
| T13 | Compile-time features | **C#** | Partial classes, source generators, `#if`, `[Conditional]`. Java has no equivalents (subclass hooks, runtime flags instead). | 72 / 55 |
| T14 | Checked exceptions | **Java** | The compiler forces callers to handle declared failures. In C# a forgotten `catch` compiles and fails at runtime. | 47 / 51 |
| T15 | Enums | **Java** | Enum constants carry fields and their own method bodies, and implement interfaces. C# enums are plain ints (`(Enum)42` is legal). | 50 / 46 |
| T16 | Anonymous & inner classes | **Java** | Anonymous classes implement multi-method interfaces inline, and inner classes see the outer instance. C# needs named classes. | 70 / 50 |
| T17 | Generic variance | **Java** | Use-site `? extends` / `? super` works on any type. C# has no lower bound, so consumers need a delegate workaround. | 25 / 24 |
| T18 | Null safety | **C#** | Nullable reference types are checked by the compiler (a possible-null dereference fails the build), and `?.` `??` `??=` exist. Java has only `Optional` chains and NPEs at runtime. | 33 / 12 |
| T19 | Iterators & generators | **C#** | `yield return` and async streams (`await foreach`) generate the state machine. Java needs a hand-written `Iterator` (though `Stream.iterate` covers the simple cases). | 55 / 58** |
| T20 | Expression trees | **C#** | A plain lambda can be inspected and translated to SQL, as EF Core does. Java lambdas are opaque, so you need a Criteria/metamodel DSL (JPA Criteria, QueryDSL). | 55 / 37 |
| T21 | Memory & value types | **C#** | 1M struct points = 16 bytes each in one allocation, and `Span`/`stackalloc` parse with 0 allocations. Java: ~36 bytes and 1M objects, and its workaround is parallel primitive arrays. | 50 / 26 |
| T22 | Resource cleanup | **Tie** | C# `using var` needs no nesting. Java try-with-resources keeps the original exception and records `close()` failures as suppressed; in C# the `Dispose` exception replaces the original. | 34 / 37 |
| T23 | Collection expressions | **C#** | One `[promo, ..bestSellers, ..newArrivals]` syntax builds arrays, lists, sets and immutable collections. Java mixes `List.of`, `addAll` and streams per type, and `List.of(...).add()` compiles but throws. | 20 / 9 |
| T24 | Labeled break & continue | **Java** | `break search;` / `continue orders;` leave or skip an outer loop directly. C# needs `goto`, a flag variable or an extra method. | 29 / 42 |

### Performance: measured results

How these numbers were produced: see [Methodology](#performance-and-other-measured-aspects-setup), which also lists the commands.

| Measurement | Java 25 | Java 21 *(reference)* | C# / .NET 10 |
|-------------|:-------:|:---------------------:|:------------:|
| All 24 demos: wall time | 637 ms | 653 ms | 502 ms |
| All 24 demos: peak RAM | 147 MB | 124 MB | 47 MB |
| Topic 01 only (mostly runtime startup): wall time / peak RAM | 154 ms / 53 MB | 159 ms / 44 MB | 57 ms / 20 MB |
| Topic 21 only (1M points): wall time / peak RAM | 152 ms / 96 MB | 184 ms / 118 MB | 61 ms / 35 MB |
| Topic 21 with compact object headers (`-XX:+UseCompactObjectHeaders`) | 155 ms / 89 MB | — | — |
| Topic 21: bytes allocated per point | 36 (28 with compact headers) | 36 | 16 |
| Topic 08 only (includes the fake remote-call waits) | 464 ms | 487 ms | 286 ms |
| Test suite, 104 tests: command wall time | 4.2 s | 4.5 s | 2.5 s |
| Clean build of the main code | 3.8 s | 4.5 s | 3.3 s |
| Maven's own startup (included in the two rows above) | 1.6 s | 1.8 s | — |

> [!NOTE]
> **How to read these numbers:**
> - **Small, short-running programs favour .NET on every row.** It starts faster and needs less memory for the same work, and value types avoid a heap object per element (T21).
> - **Java 25 is faster than Java 21 on every time row:** T21 by 17%, the clean build by 15%, and tests by 8%. It is still slower than C# on all of them. Why Java 25 is faster than Java 21 with nearly the same code: see [Java 21 → Java 25](../README.md#java-21--java-25-migration) in the root README. Compact object headers cut a `Point` object from 36 to 28 bytes, still well above C#'s 16-byte struct.
> - **Java 25 had higher peak RAM than Java 21 for the full demo run and T01** (147 vs 124 MB, 53 vs 44 MB) with default settings; C# used 47 and 20 MB. The cause wasn't investigated (likely different default heap sizing or class-data sharing).
> - **The numbers don't measure long-running throughput,** where the JVM's JIT is very strong. See `spring-vs-net` for servers.
> - **Part of the demo time is deliberate waiting** for the fake remote calls in T08. The Java T08 demo runs both async styles, so it waits about 300 ms against about 200 ms for C#.
> - **Treat these as indications, not benchmarks.** They're one machine, default settings, and no JVM tuning, AOT cache or .NET Native AOT.

## When to choose which

**Short answer:** both are mature, fast, cross-platform and well supported. In practice the choice is decided more by the
platform, the existing systems and the team than by syntax. Where the language itself matters, this is what the samples show.

**Choose C# when:**
- **Less boilerplate in classes and methods (built in).** Properties and records with `with` (T01), extension methods (T06), optional/named
  arguments, `out`/`ref` and tuple returns (T10), indexers (T11), object initializers, operator overloading and user-defined
  conversions, and no checked exceptions.
- **Concise data and query code (built in).** LINQ (T04), string interpolation (T02), ranges and indices (T11), collection expressions (T23),
  tuples and deconstruction, list patterns, null operators (?. ??), `async`/`await`, `yield` and async streams (T19).
  Together with the bullet above, that's most of C#'s 15 topic wins.
- **More powerful generics (built in).** Reified generics and generic math (T03); no boxing for value types, richer constraints, default(T),
  typeof(T) and new T() work inside generic code.
  <br>Unified type system: int is a struct that derives from object, so 5.ToString() works and there is no int/Integer split.
- **Compile-time tools (built in).** Partial classes, source generators and conditional compilation (T13), lambdas that can be translated
  to SQL (T20); nameof(x), caller info attributes and Roslyn analyzers with code fixes. Exception filters and `checked`
  arithmetic (T12) handle errors more precisely.
- **You want the compiler to catch null errors.** Nullable reference types are checked at build time (T18). By default
  they are warnings; this repo turns them into errors with `<WarningsAsErrors>nullable</WarningsAsErrors>`.
- **Your platform points to .NET:** Windows desktop (WPF, WinUI), games (Unity, Godot), Microsoft- and Azure-centred companies.
- **Memory and performance-sensitive code.** Value types, `Span` and `stackalloc` avoid allocations (T05, T21: 16 vs 36 bytes
  per point), and short-running programs start faster with less RAM (see [Performance: measured results](#performance-measured-results)).
  - Note: C# wins on short runs because it starts faster and allocates less (JVM sizes its heap, structs/Span/stackalloc avoid heap). It doesn't execute the code faster.
      <br> Where it's not true: Raw execution speed, class object cost, Java closing gap with CDS/AOT and JVM tuning(-Xmx, SerialGC).
- **Native interop and low-level control matter (built in).** Unsigned types (uint, ushort, ulong), ref returns and ref locals,
  P/Invoke marshalling, function pointers, unsafe pointers, fixed/pinning and StructLayout unions. Java 22+ narrows this with the FFM API.

**Choose Java when:**
- **Compile-time safety of the design matters.** Checked exceptions force error handling (T14), sealed hierarchies make
  `switch` exhaustive (T09), and enums can't hold invalid values (T15), lambdas capturing variables that change (T07), final fields must be assigned,
  unreachable code is error instead of a warning. These are Java's wins, and they prevent runtime bugs.
  - Note: C# prevents some bugs Java doesn't: nullable reference types catch null errors at build time (T18), 
        checked arithmetic catches integer overflow (T12), no accidental switch fall-through, override is required, while Java's @Override is optional, 
        == on strings compares values, not references
- **Flexible object-oriented code (built in).** Anonymous classes implement interfaces inline and inner classes see their outer
  instance (T16), use-site variance `? super` works on any type (T17), and labeled `break`/`continue` leave nested loops
  directly (T24); Enums are full classes, Local types (class,record,enum,interface inside method), Sealed hierarchies are flexible, Virtual by default (any method can be overridden).
- **Your platform points to the JVM:** Android (with Kotlin), big data (Spark, Kafka, Flink, Hadoop), and large enterprise
  backends in banking, insurance and government.
- **You want a vendor-neutral platform with the largest library ecosystem:** many JDK vendors, the JCP, and Maven Central.
- **Long-term stability matters.** Old libraries and bytecode still run on new JDKs, and type erasure let generics be added without breaking old code. .NET had the .NET Framework vs .NET Core split.
- **Simple blocking code must scale (built in).** Virtual threads let plain blocking code handle many concurrent requests without
  async/await (T08).
- **Long-running servers.** The JIT is strong over long runs.
  - Note: Collecting profiles and compiling with C2(optimizing compiler) takes time and CPU, short program finishes before most of its code ever reaches the C2 stage.
          <br>.NET now does much of the same thing, the main remaining difference is deoptimization, .NET doesn't throw away compiled code to go back to an earlier tier.
  - Memory reclamation: SoftReference for memory-sensitive caches and PhantomReference/Cleaner for cleanup after an object
    is unreachable. C# has only weak references and finalizers.

**Before deciding, keep in mind:**
- **Java has caught up in several areas** the source notes still gave to C#: lambdas and functions (T07), async with virtual
  threads (T08) and pattern matching (T09) are ties today.
- **This compares Java the language, not the JVM.** Kotlin, on the same JVM and with the same libraries, closes most of C#'s
  syntax wins (properties, extension functions, null safety, named arguments, coroutines).
- **Neither side is trap-free** (see [Traps the tests show in both languages](#traps-the-tests-show-in-both-languages)).

## Limitations

- **"Better" is an opinion, built on tested facts.** The tests prove the facts behind each verdict. Which side is better is decided with the criteria in [Methodology](#how-the-winner-of-a-topic-is-decided), and someone who values them differently could pick the other side.
- **The samples are small and in one domain** (a shop). Effects that only show in large codebases or large teams aren't covered.
- **Code lines are not an exact measurement.** They depend on the brace style of both languages.
- **Measurements come from one machine** (Ryzen 5 5600H, 15.3 GB RAM, Windows 11), with default settings and the median of
  3–5 runs. They measure short-running programs, not long-running JIT performance or tuned setups. Differences below about 5%
  are within run-to-run noise.
- **This compares Java the language, not the JVM.** Kotlin, Scala and other JVM languages aren't covered.
- **The platform and ecosystem verdicts aren't tested or measured.** The rows in [Platform, tooling & ecosystem](#platform-tooling--ecosystem) (Android, games, libraries, community, job market) are based on public information, not on the samples.

## Source notes: Covered by samples

This project is based on research into many published articles that compare the two languages and draw conclusions.
The authors' conclusions from those articles were merged into one set of notes. The code samples set out to
cover and support those conclusions, or to correct them where they are outdated. The table below lists each point from those notes that has a topic,
and where it is shown. Points without a topic are in [Source notes: Not covered by samples](#source-notes-not-covered-by-samples).

**Added beyond the notes:**

- null safety (T18)
- `yield` and async streams (T19)
- resource cleanup (T22)

> [!NOTE]
> When today's verdict is not the same as the one in the notes, the row gets one of these labels:
>
> - *(note outdated)*: the notes named C# as better, but Java has caught up since the articles were written, so it is now a tie.
> - *(partly outdated)*: the notes named Java as better, and that is now true only in part.

| From the notes | Where | Better |
|----------------|-------|:------:|
| Unified type system, primitives with methods, `List<int>`, generic specialization, `where T : new()` | T03 | C# |
| Properties | T01 | C# |
| LINQ and lambdas | T04 | C# |
| LINQ against a database (expression trees) | T20 | C# |
| Operator overloading | T05 | C# |
| Delegates and events | T07 | Tie *(note outdated)* |
| `dynamic` | T13 | C# (rarely the right tool) |
| `ushort`/unsigned types, `struct`, value types, memory control, `stackalloc` | T05, T21 | C# |
| `async` / `await` | T08 | Tie (virtual threads) *(note outdated)* |
| Extension methods | T06 | C# |
| Several public classes per file | Layout: 89 Java files vs 54 C# files | C# |
| Partial classes and partial methods | T13 | C# |
| Conditional compilation, `[Conditional]` methods | T13 | C# |
| String interpolation | T02 | C# |
| `ref`, `out`, optional and named arguments | T10 | C# |
| Indexers | T11 | C# |
| Exception filters, overflow control (`checked`) | T12 | C# |
| Multi-catch `catch (A \| B e)` | T12 | Java |
| Collection expressions / spread `[..a, ..b]` | T23 | C# |
| Labeled `break` / `continue` | T24 | Java |
| Pattern matching | T09 | Tie *(note outdated)* |
| Checked exceptions | T14 | Java |
| Enums implementing interfaces | T15 | Java |
| Instance-level inner classes | T16 | Java |
| `? super` (lower-bounded wildcards) | T17 | Java |
| Default interface methods | T16 (`DiscountRule.apply` / `IDiscountRule.Apply`) | Tie, both have them *(note outdated)* |
| "Java type safety is really safe" | T09, T14, T15 support it; T03 and T18 show the opposite | Mixed *(partly outdated)* |

## Traps the tests show in both languages

A trap is code that compiles and looks correct, but does something unexpected at runtime. The tests also include
each language's traps, so the comparison stays fair.
**They show the price of each winning feature.** The topics table only says which side is better. But a feature
that wins can still hurt you, and the table below shows where.
So a reader sees "C# is better here" and also "but watch out for this". That's what "fair" means: C# wins 15
topics, but it isn't trap-free, and neither is Java.

### All traps by topic

Every trap below is asserted by a test, so each one is shown to be real on Java 25 and C# 14.\
Java has traps in 7 topics and C# in 9.

> [!NOTE]
> The table uses these marks:
>
> - `—`: that side has no trap in this topic, usually because the compiler catches the mistake.
> - Topics without a trap on either side are left out.

| T# | Topic (winner) | Java trap | C# trap |
|----|----------------|-----------|---------|
| T03 | Type system & generics (**C#**) | `Integer 1000 == 1000` is `false`, but `100 == 100` is `true` (only -128..127 are cached). `==` on two equal `String`s is `false`. A `null` in a `List<Integer>` throws NPE on unboxing. Erasure says an empty `List<Integer>` is a list of strings. | Boxing to `object` still breaks `==`: `(object)1000 == (object)1000` is `false`. |
| T05 | Operators & value types (**C#**) | `new BigDecimal("2.5").equals(new BigDecimal("2.50"))` is `false`. The elements of `new Money[3]` are `null`. `(byte) 0xFF` is `-1`, so unsigned bytes need `& 0xFF`. | The elements of `new Money[3]` are `default(Money)`, which skips the constructor, so `Currency` is `null`. |
| T07 | Events & functions (**Tie**) | — (the compiler rejects capturing a loop variable) | Closures in a `for` loop share one `i`, so they return `[3, 3, 3]`. |
| T08 | Async (**Tie**) | `CompletableFuture.join()` wraps the error in `CompletionException`, so `catch (NoSuchElementException e)` misses it. | — (`await` rethrows the original exception) |
| T12 | Exception filters & overflow (**C#**) | `int` overflow wraps silently: `50_000 * 99_999` gives `704_982_704`. | The same, unless you use `checked`. |
| T13 | Compile-time features (**C#**) | An untyped `Map<String, Object>` accepts `"twenty"` as a page size and throws `ClassCastException` only when it is read. | `dynamic` accepts `"twenty"` and throws `RuntimeBinderException` only when it is read. |
| T14 | Checked exceptions (**Java**) | — (the compiler forces the `catch`) | A forgotten `catch` compiles without a warning and fails at runtime. |
| T15 | Enums (**Java**) | — (invalid values can't exist) | `(ShippingMethod)42` compiles, and `Enum.Parse<ShippingMethod>("42")` "succeeds". |
| T18 | Null safety (**C#**) | `customer.address().city()` compiles and throws NPE. | — (nullable reference types make it a compile error) |
| T20 | Expression trees (**C#**) | — (the hand-made query DSL only accepts what it can translate) | A lambda the translator can't map to SQL compiles, then throws `NotSupportedException` at runtime. |
| T22 | Resource cleanup (**Tie**) | — (a `close()` failure is kept as a suppressed exception) | An exception from `Dispose` replaces the original one, so the real cause is lost. |
| T23 | Collection expressions (**C#**) | `List.of(...)` still has `add()`, which throws `UnsupportedOperationException` at runtime. | — (immutability is visible in the type) |

## Source notes: Not covered by samples

This project is based on research into many published articles that compare the two languages and draw conclusions.
The authors' conclusions from those articles were merged into one set of notes, and the code samples cover and support
as many of them as possible. This section lists the conclusions from those notes that have no topic of their own.

Some are too minor for a topic (`goto`, `#region`), some can't be tested reliably (native interop, GC-dependent references), and some aren't about code at all (performance, tooling, ecosystem). Measured numbers for the performance rows are in [Performance: measured results](#performance-measured-results). Each row gives the current state and which side is better.

> [!NOTE]
> When a conclusion from the notes no longer holds, the "Better" column gets one of these labels:
>
> - *(note outdated)*: the conclusion is no longer true; the row shows the current state.
> - *(partly outdated)*: the conclusion is still true, but the gap is much smaller than the notes say.
> - *(vague)*: the conclusion is too general to check, so the row explains what is actually true.
>
> A **bold** verdict is a real advantage. A plain one (C# has it, Java (minor)) means the feature exists on one side only, but gives little or no practical advantage. Words in plain brackets qualify the verdict:
>
> - (slightly): the advantage is real, but small.
> - (minor): the feature exists, but is rarely needed.
> - (curiosity): the feature exists, but is almost never used.
> - (legacy only): it matters only for very old code.
> - (Kotlin): the advantage belongs to the JVM platform, so Kotlin shares it.

### Language & runtime features

| From the notes | Better | Why / current state |
|-----------------------------|:------:|---------------------|
| Pointers, `unsafe`, address-of `&`, `fixed` (object pinning), fixed-size buffers | **C#** | Real low-level control for interop and hot loops. Java has no pointers by design; its Foreign Function & Memory API (final in Java 22) covers native calls and off-heap memory, without pointer syntax. T21 shows the safe everyday part (`Span`, `stackalloc`). |
| Function pointers (`delegate*`), `[StructLayout]` unions, declarative marshalling (`[LibraryImport]`) | **C#** | Calling native code is built into the language and runtime. Java's FFM API is newer and more verbose; JNI is the old, much heavier alternative. |
| Destructors / "memory control" | **Tie** | Both have finalizers, and both discourage them. Java deprecated `finalize()` in favour of `Cleaner`; C# uses `IDisposable`/`using`. Deterministic cleanup is covered in T22. |
| Structures (value types) | **C#** | `struct`, `readonly struct`, `ref struct`. Covered in T05 and T21. Java's value classes (Project Valhalla) are not in a released LTS yet. |
| `goto` | C# has it | Legal in C# (also used for `switch` fall-through), but rarely a good idea. Java reserves the word and never uses it. Not an advantage in practice. |
| `#region` / `#endregion` | C# has it | Only affects IDE code folding. Java IDEs use comment markers. Cosmetic. |
| File-scoped types (`file class`, C# 11) | **C#** | A type visible only in its own file (handy for source generators). Java's closest match is a package-private top-level class. |
| Several public types per file | **C#** | C# allows it; Java allows one public top-level class per file. Visible in the layout: 89 Java files vs 54 C# files for the same app. |
| Non-zero-based arrays | C# (curiosity) | Only through `Array.CreateInstance` with lower bounds, for COM interop. Almost never used. |
| Tail-call optimization | **Neither** | Neither *language* guarantees it. The .NET JIT sometimes emits tail calls; the JVM does not. Write deep recursion as loops on both. |
| Conditional metadata (attributes kept only for some build symbols) | **C#** | `[Conditional]` on attribute classes. Niche; related features are covered in T13. |
| Default interface methods "limited in Java" | **Tie** *(note outdated)* | Both have had them for years (Java 8, C# 8). Neither allows instance state in interfaces. C# adds `static abstract` members (used for generic math in T03). |
| `strictfp` (same floating point everywhere) | **Neither** *(note outdated)* | Since Java 17 all floating-point math is strict, so `strictfp` does nothing. .NET follows IEEE 754 too. |
| Built-in annotation processing vs source generators | **Tie** | Both have compile-time code generation: Java annotation processors, C# source generators (T13 uses `[GeneratedRegex]`). |
| Reflection & runtime metaprogramming "weaker in C#" | **Tie** *(note outdated)* | Both are complete and mature. Spring and Hibernate lean on Java reflection; .NET is moving to source generators so apps can be trimmed and compiled AOT. |
| Soft references (`SoftReference`) | **Java** | Kept until memory runs low: a simple memory-sensitive cache. C# has only `WeakReference` (collected at the next GC); caches use `MemoryCache` with size limits instead. Not tested because GC timing isn't deterministic. |
| Phantom references (`PhantomReference`, `Cleaner`) | **Java** | Notification after an object becomes unreachable, without resurrection risk. C# has finalizers and `ConditionalWeakTable`, but no phantom reachability. |
| Instance initializer blocks `{ ... }` | Java (minor) | Run as part of every constructor. C# covers the same need with constructor chaining and field initializers. Java's "double-brace initialization" built on this is an anti-pattern. |
| Raw types / "migration compatibility" of erasure | Java (legacy only) | Pre-2004 code without generics still links against generic code. It only matters for very old libraries; the cost is erasure (T03). |
| "Robustness: handles errors automatically" | **Neither** *(vague)* | Neither runtime handles errors for you. Java's checked exceptions (T14) force handling; C#'s nullable types (T18) prevent a whole class of errors. |
| "Type safety is really safe" | **Mixed** | Java is stricter on checked exceptions, sealed switches and enums (T09, T14, T15). C# is stricter on nulls and generics (T18, T03). |
| Performance ("C# good performance") | **C#** (slightly) | Value types, `Span`, stack allocation and reified generics avoid allocations (T21 measures 16 vs 36 bytes per point). The JVM's JIT is excellent for long-running servers, so real-world gaps depend on the workload. Benchmark your own case. |
| Less memory usage | **C#** | Struct arrays have no per-element headers (T21: 16 bytes per point vs 36 on Java 25, or 28 with compact object headers), and a .NET process usually starts smaller than a JVM. The JVM has CDS, the AOT cache and GraalVM Native Image to close the gap. |
| Startup time | **C#** | .NET usually starts faster; both have native AOT (.NET Native AOT, GraalVM Native Image). The spring-vs-net apps start in ≈ 8.4 s (Spring on Java 25; 3.2 s with the AOT cache) vs ≈ 2.1 s (ASP.NET) on the used machine (see its Performance: measured results). |
| "Slow evolution of Java" | **C#** *(partly outdated)* | C# shipped properties, LINQ, async, records and patterns years earlier. Since 2018 Java releases every 6 months (records, sealed types, patterns, virtual threads), so the gap is much smaller now. |

### Platform, tooling & ecosystem

| From the notes | Better | Why / current state |
|-----------------------------|:------:|---------------------|
| Runs on any platform | **Java** (slightly) *(partly outdated)* | Java has been cross-platform from day one. .NET has run on Windows, Linux and macOS since .NET Core (2016) and is routine in Linux containers. Windows-only parts remain: WPF and WinForms desktop UI (MAUI and Avalonia are cross-platform). |
| Android / mobile | **Java** (Kotlin) | Android's native language is Java/Kotlin. .NET MAUI targets Android and iOS, but its ecosystem is smaller. |
| Video games | **C#** | Unity and Godot (C#) use it. Java's game presence is mostly Minecraft and LibGDX. |
| Windows desktop apps | **C#** | WPF, WinForms, WinUI. Java's Swing/JavaFX are cross-platform but less common today. |
| Scientific / big data / data centers | **Java** | The JVM big-data ecosystem (Hadoop, Spark, Kafka, Flink, Elasticsearch) is Java/Scala. .NET has ML.NET and Spark bindings, but a smaller community. |
| Enterprise and cloud backends | **Tie** | Both are first-class. Java + Spring dominates in banking, insurance and government; .NET is strong in Microsoft-centric companies. All major clouds support both. |
| IDE / tooling | **Tie** | Java: IntelliJ IDEA (the reference), Eclipse, VS Code. C#: Visual Studio (Windows; VS for Mac was retired in 2024), JetBrains Rider (free for non-commercial use), VS Code with C# Dev Kit. Both are excellent. |
| Library ecosystem size | **Java** | Maven Central is much larger than NuGet, and there are usually several mature options per problem. |
| Version fragmentation | **Java** (slightly) *(partly outdated)* | The .NET Framework vs .NET Core split was painful; since .NET 5 there is one line with an LTS every two years. Java had its own breaks (Java 9 modules, `javax` → `jakarta`), but old libraries usually still run. |
| Open-source community | **Java** | Decades of Apache and Eclipse Foundation projects. .NET became open source in 2014 and has a growing community, still more centred on Microsoft. |
| Governance / vendor lock-in | **Java** | OpenJDK with many vendors (Oracle, Red Hat, Amazon, Microsoft, Azul...) and the JCP. C# and .NET are led by Microsoft, though designed in the open (dotnet/csharplang, .NET Foundation). |
| Documentation | **Tie** *(note outdated)* | Both are excellent: the JDK Javadoc and OpenJDK JEPs vs Microsoft Learn, which is among the best official docs of any platform. |
| Community support / Q&A volume | **Java** | More material overall, and more years of it. C# is also very well covered. |
| Standardized APIs, "no new framework to learn" | **Tie** *(vague)* | Both have large standard libraries (JDK vs BCL). Web and data work in either needs a framework (see spring-vs-net). |
| Learning resources, universities | **Java** | Java is more common in university courses and textbooks. A market fact, not a language property. |
