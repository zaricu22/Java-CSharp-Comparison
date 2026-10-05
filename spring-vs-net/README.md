# Spring Boot vs ASP.NET Core — one API, two frameworks

This project is based on research into many published articles that compare Spring Boot and ASP.NET Core. The
authors' conclusions from those articles were merged into one set of notes, and the aim of this project is to back
each conclusion with working code: wherever a claim can be shown in code, it becomes a small runnable sample, with
tests on both sides, so it can be checked rather than taken on trust. Where a conclusion turned out to be outdated,
the samples show the current state instead.

The same **shop REST API** (products, orders, payments, reports, admin) is built twice:
**Spring Boot 4.0 (Spring Framework 7, Java 25)** and **ASP.NET Core 10 (C# 14)**. Both use the same
endpoints, the same seed data and the same test scenarios. Each side is written the way you would build
a real service today (records, virtual threads, minimal APIs, first-party libraries where they exist),
so the differences come from the frameworks, not from a weak implementation.

It has 21 topics. Every topic is a package (`shop.tNN_…`) or folder (`TNN_…`) with the same number, plus a test
class covering the same scenarios.

Every `.java` and `.cs` file (sources and tests) starts with the same two-line verdict for its topic:

```java
// VERDICT | T09 AOP / cross-cutting | BETTER: SPRING
// WHY: an @Aspect intercepts any bean method, including the service layer; ASP.NET endpoint filters and middleware only wrap HTTP requests.
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
- [Results: which framework is better and why (by topic)](#results-which-framework-is-better-and-why-by-topic)
  - [Performance: measured results](#performance-measured-results)
- [When to choose which](#when-to-choose-which)
- [Limitations](#limitations)
- [Source notes: Covered by samples](#source-notes-covered-by-samples)
- [Things found while building it (both directions)](#things-found-while-building-it-both-directions)
- [Source notes: Not covered by samples](#source-notes-not-covered-by-samples)
  - [Framework features](#framework-features)
  - [Runtime & performance](#runtime--performance)
  - [Developer experience](#developer-experience)
  - [Ecosystem & community](#ecosystem--community)

## Running it

Requirements: **JDK 25+** with **Maven 3.9+**, and the **.NET 10 SDK**. Both apps use an in-memory database
(H2 / SQLite), so there is nothing else to install.

> [!NOTE]
> **What running the scripts doesn't show:**
> - **Which side is better.** Passing tests prove the facts behind a verdict. The verdict itself (for example "@Transactional beats manual transactions") is a judgement, made with the [Methodology](#how-the-winner-of-a-topic-is-decided) criteria.
> - **Performance.** Test time from these scripts isn't a measurement. The numbers come from the `tools/Measure-*.ps1` scripts: median of several runs, peak RAM, startup, throughput (see [Performance: measured results](#performance-measured-results)).
> - **That the code is the best possible.** Only that it compiles and behaves as claimed.

**So the evidence order is:**
1. **Tests** prove the claims automatically. Real proof is in the test code.
2. **Demos** (languages only, console) show that both sides produce the same or different results (printed output): see [java-vs-c#](../java-vs-c%23/README.md#running-it).
3. **Running the app** (frameworks only, server) lets you explore by hand, things like: Error format (T04), Validation errors (T04),
   Security (T12), Rate limiting (T13), API versioning (T19), Pagination (T17), Health (T14), OpenAPI (T16),
   Output caching (T21), User accounts (T20), Startup log.

```powershell
./run.ps1                          # all tests: Spring (61) + ASP.NET (60)
./run.ps1 -Side dotnet             # one side only
./run.ps1 -Mode run -Side spring   # start the Spring app   -> http://localhost:8080
./run.ps1 -Mode run -Side dotnet   # start the ASP.NET app  -> http://localhost:5080
```

On Linux, macOS or Git Bash, `run.sh` does the same:

```bash
./run.sh                           # all tests: Spring (61) + ASP.NET (60)
./run.sh --side dotnet             # one side only
./run.sh --mode run --side spring  # start the Spring app   -> http://localhost:8080
./run.sh --mode run --side dotnet  # start the ASP.NET app  -> http://localhost:5080
```

Both scripts find a JDK 25+ by themselves, because Maven uses `JAVA_HOME` and that often points to an older JDK.
Set `JAVA25_HOME` to choose a specific JDK.

Try the running app (same paths on both; change the port to 5080 for the ASP.NET app):

```bash
curl localhost:8080/api/products/E1
curl localhost:8080/api/reports/revenue-by-category
curl -X POST localhost:8080/api/orders -H "Content-Type: application/json" -d '{"customer":"Eva","lines":[{"sku":"B1","quantity":2}]}'
curl -X POST -u admin:admin-pass "localhost:8080/api/admin/products/E1/restock?quantity=10"
curl localhost:8080/actuator/health      # ASP.NET: /health
curl localhost:8080/v3/api-docs          # ASP.NET: /openapi/v1.json
curl "localhost:8080/api/products/paged?page=0&size=2&sort=price,desc"
curl -H "X-API-Version: 2" localhost:8080/api/catalog/E1
```

## Project layout

Both sides implement the same endpoints, with the same seed data and the same test scenarios. Where one side has
extra types (the .NET event bus in T08, the Basic handler in T12, Spring's rate-limit filter in T13 and its account
store in T20), that code is what the other framework provides built in, so the extra code **is** the difference.
The Spring side has **63** files under `src/main/java` and the ASP.NET side has **28** `.cs` files under
`src/Shop.Api`: C# allows several public types per file, while Java needs one file per public type.

```
spring-vs-net/
├── README.md
├── run.ps1                                  # tests or run, for one or both sides
├── run.sh                                   # the same for Linux, macOS and Git Bash
│
├── spring/                                  # Spring Boot 4.0.2 · Java 25 · Maven · JUnit + MockMvc
│   ├── pom.xml
│   └── src/
│       ├── main/java/shop/
│       │   ├── ShopApplication.java         # @SpringBootApplication + @EnableScheduling/@EnableResilientMethods
│       │   ├── domain/                      # ProductEntity, OrderEntity, OrderLineEntity (JPA), Category, InsufficientStockException, DataSeeder
│       │   ├── t01_endpoints/               # ProductController, CatalogService, ProductDto, RegisterResult (sealed)
│       │   ├── t02_di/                      # PaymentProvider + PaymentProviders (3 scanned beans), PaymentService (Map injection), RequestInfo, RequestLogger, DiController
│       │   ├── t03_config/                  # ShopProperties (@ConfigurationProperties), NotificationSenders (@Profile), RecommendationService (@ConditionalOnProperty), ConfigController
│       │   ├── t04_validation/              # NewProduct (Bean Validation), ValidationProblemAdvice
│       │   ├── t05_repositories/            # ProductRepository (derived queries), InventoryController
│       │   ├── t06_queries/                 # OrderRepository (JPQL), OrderSpecifications (Criteria), CategoryRevenue, CustomerTotal, ReportController
│       │   ├── t07_transactions/            # OrderService (@Transactional), OrderRequest, OrderController
│       │   ├── t08_events/                  # OrderPlaced, StockAlerts (@EventListener), OrderAuditLog (@TransactionalEventListener)
│       │   ├── t09_aop/                     # Audited (annotation), AuditAspect (@Around)
│       │   ├── t10_scheduling/              # MaintenanceJobs (@Scheduled fixedRate + cron)
│       │   ├── t11_resilience/              # ExchangeRateClient (@Retryable), FlakyRatesApi, RateController
│       │   ├── t12_security/                # SecurityConfig (httpBasic, in-memory users), InventoryService (@PreAuthorize), AdminController
│       │   ├── t13_ratelimiting/            # SearchRateLimitFilter (hand-written), SearchController
│       │   ├── t14_health/                  # InventoryHealthIndicator (Actuator)
│       │   ├── t16_openapi/                 # OpenApiConfig (springdoc)
│       │   ├── t17_pagination/              # ProductPageController (Pageable -> PagedModel)
│       │   ├── t18_httpclients/             # SupplierApi (@HttpExchange interface), SupplierClientConfig, ReorderService
│       │   ├── t19_versioning/              # ApiVersioningConfig (header + deprecation), CatalogVersionsController
│       │   ├── t20_accounts/                # AccountEntity, AccountRepository, AccountService, AccountSecurityConfig, AccountController (all hand-written)
│       │   └── t21_caching/                 # CacheConfig (@EnableCaching), CachedCatalog (@Cacheable/@CacheEvict), CachedCatalogController
│       ├── main/resources/                  # application.yml, application-prod.yml
│       └── test/java/shop/                  # t01 … t21: one or more test classes per topic
│           └── t15_testing/                 # @WebMvcTest slice + @MockitoBean
│
└── dotnet/                                  # ASP.NET Core 10 · C# 14 · xUnit + WebApplicationFactory
    ├── SpringVsNet.slnx
    ├── global.json                          # pins SDK 10.x
    ├── Directory.Build.props
    ├── src/Shop.Api/
    │   ├── Shop.Api.csproj
    │   ├── Program.cs                       # explicit wiring of every module, middleware and endpoint group
    │   ├── appsettings.json, appsettings.Production.json
    │   ├── Properties/launchSettings.json   # `dotnet run` profile: Development environment, port 5080
    │   ├── Domain/                          # Entities.cs (plain classes), ShopDbContext.cs (EF Core + Identity tables), DataSeeder.cs
    │   ├── T01_Endpoints/                   # ProductEndpoints.cs (minimal API), CatalogService.cs: CatalogService, ProductDto, RegisterResult
    │   ├── T02_Di/                          # Payments.cs: IPaymentProvider x3 (keyed), PaymentService, RequestInfo, RequestLogger, DiModule
    │   ├── T03_Config/                      # ShopOptions.cs (IOptions + ValidateOnStart), ConfigModule.cs: notification senders, RecommendationService, if/else registrations
    │   ├── T04_Validation/                  # NewProduct.cs: NewProduct (DataAnnotations; AddValidation)
    │   ├── T05_Repositories/                # ProductQueries.cs: ProductQueries (IQueryable extensions), InventoryEndpoints
    │   ├── T06_Queries/                     # OrderReports.cs: OrderReports (LINQ GroupBy/Sum, conditional Where), CategoryRevenue, CustomerTotal
    │   ├── T07_Transactions/                # OrderService.cs: OrderService (BeginTransaction/Commit), OrderRequest, OrderModule
    │   ├── T08_Events/                      # Events.cs: OrderPlaced, hand-written IEventPublisher + IEventHandler<T>, StockAlerts, OrderAuditLog
    │   ├── T09_Aop/                         # AuditFilter.cs: AuditFilter (IEndpointFilter), AuditTrail
    │   ├── T10_Scheduling/                  # ReservationCleanupJob.cs: ReservationCleanupJob (BackgroundService + PeriodicTimer)
    │   ├── T11_Resilience/                  # Rates.cs: ExchangeRateClient (Microsoft.Extensions.Resilience pipeline), FlakyRatesApi
    │   ├── T12_Security/                    # BasicAuthenticationHandler.cs (hand-written), InventoryService.cs: RestockRequirement/Handler, InventoryService, SecurityModule.cs
    │   ├── T13_RateLimiting/                # RateLimitingModule.cs: AddRateLimiter policy + RequireRateLimiting
    │   ├── T14_Health/                      # HealthModule.cs: InventoryHealthCheck + JSON response writer
    │   ├── T16_OpenApi/                     # OpenApiModule.cs: AddOpenApi / MapOpenApi
    │   ├── T17_Pagination/                  # Paging.cs: Paging (sort whitelist, Skip/Take, count), PageResult<T> - by hand
    │   ├── T18_HttpClients/                 # SupplierClient.cs: SupplierClient (typed HttpClient, hand-written), ReorderService
    │   ├── T19_Versioning/                  # VersioningModule.cs: header parsing + Deprecation/Sunset, ProductV1/ProductV2 - by hand
    │   ├── T20_Accounts/                    # AccountsModule.cs: Identity + MapIdentityApi, Basic-or-Bearer scheme
    │   └── T21_Caching/                     # OutputCachingModule.cs: AddOutputCache, CacheOutput, EvictByTag
    └── tests/Shop.Api.Tests/                # T01 … T21, same scenarios as the Spring tests
        └── T15_Testing/                     # ShopApiFactory (WebApplicationFactory) + fake service swap
```

## Methodology

### How the winner of a topic is decided

Each topic gets **Spring**, **ASP.NET** or **Tie**. The criteria, in order of weight:

1. **What the framework gives you out of the box.** Built-in and first-party beats a third-party library, which beats hand-written code. Examples: Spring Data's derived queries vs hand-written LINQ (T05), ASP.NET's built-in rate limiter vs a hand-written servlet filter (T13).
2. **Declarative vs manual.** One annotation or one registration line that covers every call path beats code that must be repeated or called by hand. Examples: `@Transactional` (T07), `@PreAuthorize` (T12) vs an explicit `IAuthorizationService` call.
3. **Safety and correctness.** Compile-time checks beat startup-time checks, which beat runtime failures. Examples: LINQ vs JPQL strings (T06), config validated at startup (T03).
4. **Code needed for the same behaviour, and how readable it is.** Code lines are supporting evidence, not the decider. A shorter version that skips a check (validation, security, a whitelist) doesn't win.
5. **Proof by tests.** Every in-file claim on which side is better is exercised by a test on both sides, including behaviour that is *missing*. Examples: no lockout in Spring (T20), a direct service call bypassing the ASP.NET endpoint filter (T09).

A topic is a **Tie** when both sides are first-party and comparably good (T11 retry), or when each side clearly wins a different part (T15 testing).\
Performance, ecosystem and popularity are **not** used for topic verdicts; they are reported separately below and in the "not covered" tables.\
The verdicts are a judgement, written down with a reason (the `WHY:` line in every file), so you can check the code and disagree.

### Versions compared

| | Spring side | ASP.NET side |
|---|---|---|
| Framework | **Spring Boot 4.0.2** (Spring Framework 7.0.3, Nov 2025 major) | **ASP.NET Core 10** (10.0.12, LTS, Nov 2025) |
| Language / runtime | **Java 25** (Oracle JDK 25.0.4.1, LTS, Sept 2025) | **C# 14 / .NET 10.0.12** (LTS, Nov 2025; SDK 10.0.401) |
| Web server | Tomcat 11.0.15, virtual threads on | Kestrel |
| Data access | Spring Data JPA 4.0.2, Hibernate 7.2.1, H2 2.4.240 (in-memory) | EF Core 10.0.12, SQLite in-memory (Microsoft.Data.Sqlite 10.0.12) |
| Security | Spring Security 7.0.2 | ASP.NET Core authentication/authorization, Identity 10.0.12 |
| Other libraries | springdoc-openapi 3.0.3 (T16) | Microsoft.Extensions.Resilience 10.10.0 (T11), Microsoft.AspNetCore.OpenApi 10.0.12 (T16) |
| Build / tests | Maven 3.9.16; JUnit 6.0.2 + MockMvc, Mockito (Boot-managed) | .NET SDK; xUnit 2.9.3 + WebApplicationFactory (Mvc.Testing 10.0.12) |

**Frameworks and runtimes are the same generation:** the November 2025 framework majors, each on its autumn-2025 LTS runtime.

> [!NOTE]
> **Database:**
> - Both apps use an **embedded, in-memory** database that runs inside the app process, so no database server is needed:
>   - H2 (written in Java) on the Spring side, auto-configured by Spring Boot with a connection pool
>   - SQLite (a C library) on the ASP.NET side, with an explicit connection string in `ShopDbContext.cs`
> - The schema is created at startup, and the same database is used in every profile.

> [!NOTE]
> **Java version migration:**
> - The Spring side started on Java 21 and was moved to Java 25. The Java 21 numbers are kept in
>   [Performance: measured results](#performance-measured-results) for reference.
> - **What moving to Java 25 changed in the code** (verdicts unchanged): nothing. Only `<java.version>` in `pom.xml` went
>   from 21 to 25; the same code compiles and passes all 61 tests on Java 25.

### How "code lines" are counted

The numbers come from the script [`tools/Measure-CodeLines.ps1`](../tools/Measure-CodeLines.ps1), which lives at the repository root and is shared by both projects:

```powershell
./tools/Measure-CodeLines.ps1 -Project spring-vs-net
```

- **Counted:** every line of a topic's main source files (`spring/src/main/java/shop/tNN_*` vs `dotnet/src/Shop.Api/TNN_*`).
- **Not counted:**
  - blank lines
  - comment-only lines (`//`, `/*`, `*`, `///`, and `#` in YAML)
  - Java `import`/`package` lines, and C# `using` *directives* and `namespace` lines. A C# `using var x = …` statement is code and **is** counted.
  - build output
- **Tests are counted separately**, shown in the T15 row: 633 Spring vs 682 ASP.NET lines.
- **Entry point + configuration:** `ShopApplication.java` + `application*.yml` count 48 lines; `Program.cs` + `appsettings*.json` count 76.
- **Identical samples:** both sides implement the same endpoints and behaviour, with the same seed data. Helper code such as the fake remote APIs (`FlakyRatesApi` in T11, `ReorderService` in T18) exists on both sides and is counted on both.
- **Brace style is kept as written** (C# `{` on its own line), which makes C# look slightly longer.
- **When one framework needs extra hand-written code, that code is counted on purpose,** because it *is* the difference. Examples: the .NET event bus (T08), the Basic authentication handler (T12), Spring's account store (T20).

### Performance and other measured aspects: setup

The results are in [Performance: measured results](#performance-measured-results), under the topic table.

- **Machine:** AMD Ryzen 5 5600H (6 cores / 12 threads), 15.3 GB RAM, Windows 11.
- **Spring:** Java 25, with no JVM flags (default G1 GC and heap sizing). Started three ways:
  - `java -jar spring-shop-1.0.0.jar --spring.profiles.active=prod` (the fat jar)
  - the extracted jar
  - the extracted jar with a Java 25 **AOT cache** (`-XX:AOTCache`, created by one training run)
- **ASP.NET:** `dotnet Shop.Api.dll` (Release build), environment `Production`, default settings (Server GC).
- **Startup:** from process start to the first `200` on `GET /api/products/E1`. Median of 3 runs.
- **RAM:** working set 0.5 s after that first response, before any load.
- **Tests and builds:** median of 3 runs of the command.
- **Load:** [`tools/loadgen`](../tools/loadgen), a small .NET `HttpClient` load generator with 32 keep-alive connections, after a warm-up. The same neutral client is used for both servers.

**Running performance measures (reproducible):**

All scripts are in [`tools/`](../tools):
- `Measure-Run.ps1`: median wall time and exact peak RAM of a command.
- `Measure-Server.ps1`: startup, RAM, and throughput per endpoint, using `loadgen`.
- `Measure-CodeLines.ps1`: the code-line counts.

```powershell
# [setup] build the deployables first (JAVA_HOME = a JDK 25)
cd spring-vs-net/spring; mvn -q package -DskipTests
cd spring-vs-net/dotnet; dotnet build src/Shop.Api -c Release

# [performance - startup + memory + throughput] servers (3 runs each; endpoints /api/payments, /api/products/E1, revenue report)
./tools/Measure-Server.ps1 -Label Spring -Exe java -Port 18080 -Dir spring-vs-net/spring `
    -Arguments "-jar target/spring-shop-1.0.0.jar --server.port=18080 --spring.profiles.active=prod"
./tools/Measure-Server.ps1 -Label AspNet -Exe dotnet -Port 18081 -Dir spring-vs-net/dotnet/src/Shop.Api/bin/Release/net10.0 `
    -Arguments "Shop.Api.dll" -Environment @{ ASPNETCORE_URLS = "http://localhost:18081"; ASPNETCORE_ENVIRONMENT = "Production" }

# [performance - startup + memory] extracted jar: extract once, then start from the extracted files
java -Djarmode=tools -jar target/spring-shop-1.0.0.jar extract --destination target/extracted
./tools/Measure-Server.ps1 -Label "Spring extracted" -Exe java -Port 18080 -Dir spring-vs-net/spring -Endpoints /api/products/E1 -Requests 1000 -Warmup 200 `
    -Arguments "-jar target/extracted/spring-shop-1.0.0.jar --server.port=18080 --spring.profiles.active=prod"

# [performance - startup + memory] Java 25 AOT cache: one training run on the extracted jar (writes target/app.aot), then start with the cache
java -XX:AOTCacheOutput=target/app.aot -Dspring.context.exit=onRefresh -jar target/extracted/spring-shop-1.0.0.jar
./tools/Measure-Server.ps1 -Label "Spring AOT" -Exe java -Port 18080 -Dir spring-vs-net/spring -Endpoints /api/products/E1 -Requests 1000 -Warmup 200 `
    -Arguments "-XX:AOTCache=target/app.aot -jar target/extracted/spring-shop-1.0.0.jar --server.port=18080 --spring.profiles.active=prod"

# [developer loop - test time] no rebuild
./tools/Measure-Run.ps1 -Exe cmd    -Arguments "/c mvn -o -q -B surefire:test" -Dir spring-vs-net/spring -Runs 3
./tools/Measure-Run.ps1 -Exe dotnet -Arguments "test --no-build --nologo"      -Dir spring-vs-net/dotnet -Runs 3

# [developer loop - build time] clean builds
./tools/Measure-Run.ps1 -Exe cmd    -Arguments "/c mvn -o -q -B clean compile" -Dir spring-vs-net/spring -Runs 3
./tools/Measure-Run.ps1 -Exe cmd    -Arguments "/c mvn -o -q -B clean package -DskipTests" -Dir spring-vs-net/spring -Runs 3
./tools/Measure-Run.ps1 -Exe dotnet -Arguments "build src/Shop.Api -c Release --no-incremental --no-restore --disable-build-servers" -Dir spring-vs-net/dotnet -Runs 3

# [deployment - size + libraries] deployable size and number of libraries
dotnet publish spring-vs-net/dotnet/src/Shop.Api -c Release -o publish-out   # then sum the folder, count *.dll
# Spring: size of target/spring-shop-1.0.0.jar; libraries = entries under BOOT-INF/lib/ in the jar

# [performance - throughput] one endpoint by hand (after starting an app):
dotnet tools/loadgen/bin/Release/net10.0/loadgen.dll http://localhost:18080/api/payments 20000 32 5000
```

`--disable-build-servers` makes .NET build without a warm background compiler, which matches Maven (no daemon).

### Other aspects

> [!NOTE]
> - **Measured above:** startup, memory, tests, build time, deployable size, library count, and (preliminary) throughput.
> - **Not measured, but discussed in the "not covered" tables:** GC tuning and Native AOT ([Runtime & performance](#runtime--performance)), hot reload, tooling and scaffolding ([Developer experience](#developer-experience)), ecosystem, community and the job market ([Ecosystem & community](#ecosystem--community)). These aren't reproducible on one machine, so they're reported from current, public facts.

## Results: which framework is better and why (by topic)

How "code lines" are counted, which versions are compared, and the performance measurements: see [Methodology](#methodology).

**Tally: Spring 12 · ASP.NET 7 · tie 2.**\
Spring wins on **framework breadth**: declarative transactions,
events, AOP, scheduling, conditional beans, security expressions, paging, HTTP clients and API versioning.\
ASP.NET wins on **built-in web features and
language-level ergonomics**: minimal APIs, validation, LINQ, rate limiting, OpenAPI, a complete user-account system and HTTP output caching. It also stays explicit, which makes it easier to debug.

> [!NOTE]
> The table uses these marks:
>
> - *(note outdated)* in the Better column: the source notes named Spring as better, but .NET now has a first-party equivalent, so it is a tie.
> - *(not confirmed)* in the Better column: the source notes named ASP.NET as better, but the samples show the opposite.
> - All other verdicts agree with the notes, or the topic isn't in the notes (T17–T20).
> - `—` in the T# column: the domain model has no topic number, because every topic uses it.
> - `tests:` in the code lines column (T15): the Testing topic counts all test code instead of main code.

| T#  | Topic | Better | Why (concise) | Code lines Spring / ASP.NET |
|-----|-------|:------:|---------------|:-----:|
| —   | Domain model | **ASP.NET** | EF Core maps plain classes by convention. JPA needs `@Entity`/`@Id`/`@Column`, a protected no-arg constructor and getters. | 161 / 110 |
| T01 | Endpoints & hosting | **ASP.NET** | A minimal API maps a route to a lambda with typed `Results<…>`. Spring needs a `@RestController` class. (Java's sealed `switch`, since Java 21, maps results as cleanly as C#'s.) | 66 / 68 |
| T02 | Dependency injection | **Spring** | Component scanning, `Map<name, bean>` injection, and scoped proxies (a request bean inside a singleton). ASP.NET registers everything by hand, rejects scoped-in-singleton, and can't list keyed services by key. Its explicit wiring is easier to trace, though. | 86 / 53 |
| T03 | Configuration & profiles | **Spring** | `@Profile` and `@ConditionalOnProperty` swap beans declaratively. ASP.NET binds and validates options just as well, but conditional registration is hand-written `if/else` in `Program.cs`. | 77 / 82 |
| T04 | Validation & ProblemDetails | **ASP.NET** | `AddValidation()` + `AddProblemDetails()` give RFC 9457 responses with per-field errors and no extra code. Spring returns ProblemDetail too, but needs a `@RestControllerAdvice` to list the fields. | 21 / 6 |
| T05 | Repositories | **Spring** | Spring Data derives queries from method names with no implementation (`findByPriceLessThanAndStockGreaterThan…`). EF's `DbSet` already is a repository, but every named query is written as LINQ. | 24 / 22 |
| T06 | Complex queries | **ASP.NET** | LINQ `GroupBy`/`Sum`/conditional `Where` is compiler-checked and refactor-safe. Spring uses JPQL strings (checked only at boot) or the Criteria API with string attribute names. | 58 / 40 |
| T07 | Transactions | **Spring** | `@Transactional` makes the whole service method atomic across repositories and listeners. EF's `SaveChanges` is one unit of work, but anything with several saves needs `BeginTransaction`/`Commit` by hand. | 54 / 49 |
| T08 | Events | **Spring** | `ApplicationEventPublisher`, `@EventListener` and `@TransactionalEventListener` (after-commit) are built in. ASP.NET has no in-process event bus: here it's ~20 hand-written lines (MediatR went commercial in 2025). | 32 / 65 |
| T09 | AOP / cross-cutting | **Spring** | An `@Aspect` wraps any bean method, even when called from a job or another service. An ASP.NET endpoint filter wraps only HTTP endpoints that opt in; the tests show that direct service calls bypass it. | 24 / 31 |
| T10 | Scheduling | **Spring** | `@Scheduled(fixedRate / cron)` on a method. ASP.NET needs a `BackgroundService` with a `PeriodicTimer` loop and has no built-in cron. | 16 / 23 |
| T11 | Resilience (retry) | **Tie** *(note outdated)* | Spring Framework 7 has `@Retryable` built in. .NET has Microsoft.Extensions.Resilience (Polly v8) pipelines. Both are first-party today. | 56 / 60 |
| T12 | Security | **Spring** | HTTP Basic, in-memory users and `@PreAuthorize("hasRole('ADMIN') and #quantity <= 1000")` come built in. ASP.NET needs a custom Basic handler plus a requirement + handler pair, and method-level checks call `IAuthorizationService` by hand. | 46 / 107 |
| T13 | Rate limiting | **ASP.NET** | `AddRateLimiter` + `.RequireRateLimiting()`, partitioned per client, built in. Spring has no rate limiter, so this is a hand-written servlet filter (or Bucket4j). | 42 / 22 |
| T14 | Health checks | **Spring** *(not confirmed)* | Actuator returns JSON with details, plus `db` and `diskSpace` checks automatically. ASP.NET health checks are simple, but the JSON body needs a custom writer and the DB check needs another package. | 18 / 35 |
| T15 | Testing | **Tie** | Spring has slices (`@WebMvcTest`, `@DataJpaTest`, `ApplicationContextRunner`), and Mockito mocks concrete classes. ASP.NET's `WebApplicationFactory` boots the real app in memory in milliseconds, with interface-based fakes. | tests: 633 / 682 |
| T16 | OpenAPI | **ASP.NET** | `AddOpenApi()` / `MapOpenApi()` are first-party. Spring relies on the third-party springdoc project, which has to track every Boot release (3.0.x for Boot 4.0). | 7 / 9 |
| T17 | Pagination & sorting | **Spring** | Add a `Pageable` parameter: page/size/sort binding, defaults, a maximum page size (2000), the count query and totals all come from Spring Data. ASP.NET writes the sort whitelist, `Skip`/`Take`, the count and the page DTO by hand. (Spring accepts any entity property in `sort`; the hand-written .NET whitelist is stricter.) | 16 / 51 |
| T18 | Declarative HTTP clients | **Spring** | An `@HttpExchange` interface *is* the client: URLs, JSON and typed 4xx/5xx exceptions are generated, and spring-test's `MockRestServiceServer` fakes the remote API. .NET writes every call by hand in a typed `HttpClient` (Refit is third-party) and has no mock server, so the fake is a hand-written `HttpMessageHandler`. Lines are similar here; the gap grows with every extra remote endpoint. | 43 / 40 |
| T19 | API versioning | **Spring** | Spring 7 routes by version (header/path/query/media type) with one method per version, rejects unsupported versions, and sends RFC Deprecation/Sunset headers. ASP.NET has no versioning (Asp.Versioning is a separate project), so parsing, validation, branching and headers are hand-written in each endpoint. | 41 / 33 |
| T20 | User accounts | **ASP.NET** | Identity + `MapIdentityApi()`: user tables, registration, bearer + refresh tokens, password policy, PBKDF2 hashing, lockout, e-mail confirmation and 2FA endpoints. Spring Security authenticates, but accounts, registration, password rules and lockout are all hand-written, and it has no token endpoint (Spring Authorization Server is a full OAuth2 server). | 116 / 25 |
| T21 | Output caching | **ASP.NET** | `OutputCache` caches the whole HTTP response (vary by query, evict by tag) without changing the endpoint; a cache hit skips the handler, database and JSON serialization. Spring has only method-level `@Cacheable`, so each request still runs the controller and serialization, and the keys are hand-written expressions. | 52 / 41 |

### Performance: measured results

How these numbers were produced: see [Methodology](#performance-and-other-measured-aspects-setup), which also lists the commands.

| Measurement | Spring Boot 4 / Java 25 | Spring on Java 21 *(reference)* | ASP.NET Core 10 | Reliability |
|-------------|:-----------------------:|:-------------------------------:|:---------------:|-------------|
| Startup to first response: fat jar (`java -jar`) | 8.4 s | 10.1 s | 2.1 s | ✅ median of 3 |
| Startup: extracted jar | 6.9 s | — | — | ✅ median of 3 |
| Startup: extracted jar + Java 25 AOT cache | **3.2 s** | — | — | ✅ median of 3 |
| RAM after startup (idle, data seeded) | 399 MB (362 extracted, 444 with AOT cache) | 382 MB | 105 MB | ✅ before any load |
| Test suite: command wall time | 26.2 s (61 tests) | 29.6 s | 5.8 s (60 tests) | ✅ median of 3 |
| Clean build of the main code | 4.9 s (`mvn clean compile`) | 5.9 s | 4.5 s (`dotnet build --no-incremental`) | ✅ median of 3 |
| Build including the deployable | 7.5 s (`mvn clean package`, fat jar) | 9.2 s | — (`dotnet publish` after a build: 1.3 s) | ✅ |
| Deployable size | 64.4 MB fat jar (+ 142 MB AOT cache file if used) | same | 39.5 MB publish folder (9.1 MB with only the win-x64 native SQLite) | ✅ |
| Libraries shipped with the app | 94 jars | same | 22 DLLs | ✅ |
| Throughput, no-database endpoint (`GET /api/payments`), 32 connections | not re-measured | ≈ 1,580 req/s | ≈ 2,080 req/s | ⚠️ preliminary: one run, environment-limited |
| Throughput, database endpoints (`/api/products/E1`, revenue report) | not re-measured | ≈ 1,600–1,700 req/s | ≈ 1,250–1,400 req/s | ⚠️ preliminary: single short runs |

> [!NOTE]
> **How to read these numbers:**
> - **Startup and idle memory favour ASP.NET with default settings:** ASP.NET starts in 2.1 s vs 8.4 s for Spring on Java 25, and uses 105 MB vs about 400 MB. Spring's startup includes Hibernate, a dozen auto-configured subsystems (security, actuator, springdoc, ...) and class loading.
> - **Java 25's AOT cache closes most of the startup gap: 8.4 s → 3.2 s.** It costs one training run (80 s here, `-XX:AOTCacheOutput` with `-Dspring.context.exit=onRefresh`), a 142 MB cache file shipped with the app, and more RAM (444 MB vs 399 MB without the cache, and 105 MB for ASP.NET). Extracting the fat jar alone already saves 1.5 s. ASP.NET's 2.1 s uses no such step; .NET Native AOT would cut it further.
> - **Java 25 made every Java-side time faster than Java 21:** startup −17%, tests −12%, builds −16 to −19%. RAM after startup was slightly higher on Java 25 (399 vs 382 MB). ASP.NET is still ahead on all of these. Why Java 25 is faster than Java 21 with nearly the same code: see [Java 21 → Java 25](../README.md#java-21--java-25-migration) in the root README.
> - **The test suite gap (26.2 s for Spring vs 5.8 s for ASP.NET) has several causes.** Spring's test time includes about 1.6 s of Maven startup. It also builds about a dozen separate Spring application contexts: different profiles and properties, test slices, and `@DirtiesContext` for tests that change data. Each costs about 1 s. `WebApplicationFactory` boots the whole ASP.NET app in well under a second.
> - **Size and library counts are partly about where the framework lives.** The Spring jar bundles the framework itself (Tomcat, Spring, Hibernate, Jackson 2 *and* 3, ...), while ASP.NET Core and EF Core's runtime parts come from the shared .NET runtime installed on the server. Both still need a runtime (a JRE or the .NET runtime). The 32 MB of native SQLite for every OS shrinks to about 2 MB when you publish for one platform (`-r win-x64`).
> - **Throughput is limited by the used machine, not the frameworks,** and was only measured on Java 21. The machine's security layer adds about 3 ms to every localhost request, and the clean benchmark was stopped when the machine ran low on memory. The no-database endpoint shows ASP.NET about 30% ahead, in line with public benchmarks (e.g. TechEmpower). On the database endpoints Spring was ahead (≈ 1,600–1,700 vs ≈ 1,250–1,400 req/s), most likely because of the databases (H2 behind a connection pool vs in-memory SQLite), not the frameworks. Treat both throughput rows as indications only.
> - **Every number uses default settings on one machine.** Tuning (JVM flags, Server vs Workstation GC, connection pools) can change them a lot.

## When to choose which

**Short answer:** both are production-grade, first-party-supported frameworks. Spring gives you more out of the box for
*enterprise* concerns; ASP.NET Core gives you more for the *web* layer, with less memory and faster startup. In practice the
choice also follows the language, the existing systems and the team.

**Choose ASP.NET Core when:**
- **Security and traffic features (built in):** a complete user-account system (T20), rate limiting (T13) and
  HTTP output caching (T21). Spring needs hand-written code or third-party libraries for all three.
- **Web API code should be minimal (built in):** minimal APIs (T01), validation with ProblemDetails (T04) and OpenAPI (T16) need less
  code than in Spring.
- **Startup time and memory matter**, for example many small containers: 2.1 s / 105 MB vs 8.4 s / about 400 MB for Spring with
  default settings (see [Performance: measured results](#performance-measured-results)).
- **Raw throughput and less runtime tuning matter:** ASP.NET leads public benchmarks (about 30% ahead here on the no-database
  endpoint), the GC mostly needs no flags, and Native AOT works with minimal APIs out of the box.
- **Data access should be compiler-checked and simple to set up:** LINQ instead of JPQL strings (T06), plus typed includes,
  projections and bulk updates. Several databases are just one `AddDbContext<T>()` each.
  <br> Note: Java can get close with the Criteria metamodel, QueryDSL..
- **You want the compiler to catch null errors in controllers and services:** nullable reference types are checked at build
  time. Spring 7's JSpecify `@Nullable` needs extra tools like NullAway.
- **You prefer explicit wiring:** `Program.cs` shows everything that runs, DI errors name the missing service, and files can
  live anywhere. Problems are easier to trace.
- **A fast inner development loop matters:** `dotnet watch` hot reload keeps app state, and scaffolding generates controllers,
  Identity UI and EF code.
- **Outgoing HTTP calls must be resilient (built in):** `AddStandardResilienceHandler()` adds retry, circuit breaker and timeouts in one
  line. Spring's `@Retryable` (T11) has no circuit breaker.
- **Real-time features are part of the plan (built in):** SignalR gives hubs, groups, automatic reconnect and client libraries. Spring
  WebSocket + STOMP is lower-level.
- **The environment is Microsoft/Azure:** first-class Azure tooling, .NET Aspire and YARP.

**Choose Spring Boot when:**
- **Business logic needs framework support (built in):** declarative transactions (T07), in-process events (T08), AOP across
  the service layer (T09) and scheduling with cron (T10). C# needs third-party libraries.
- **Security requirements are broad (built in):** method-level security expressions (T12), plus first-party OAuth2 login, client and
  resource server, SAML 2.0, LDAP, ACLs and Spring Authorization Server.
- **You want the container to do the wiring:** auto-configuration from starters that your own beans can override, component
  scanning and Map injection of all implementations (T02), profiles and conditional beans (T03), and lifecycle hooks
  (`@PostConstruct`, `@PreDestroy`, `SmartLifecycle`).
- **Repositories should need no implementation (built in):** queries derived from method names (T05), and a `Pageable` parameter gives
  paging, sorting and totals (T17).
- **You want less hand-written plumbing for remote APIs (built in):** declarative HTTP clients (T18) and API versioning (T19).
- **Production monitoring (built in):** Actuator health with details (T14), metrics, runtime log levels and
  the conditions report.
- **The system is integration-heavy:** Spring Batch, Spring Integration, `@RabbitListener`/`@KafkaListener` with retries and
  dead-letter handling, and Apache Camel for legacy systems (SAP, mainframes). These go further than what .NET ships first-party
  (see [Source notes: Not covered by samples](#source-notes-not-covered-by-samples)).
- **You run many services that need shared infrastructure:** Spring Cloud Gateway, Config Server, service discovery and
  Kubernetes integration. .NET has Aspire and YARP, but less of it.
- **The organisation is a JVM company/team**, typically banking, insurance and government, where Java + Spring is the default.

**Before deciding, keep in mind:**
- **The "fast development (.NET) vs large enterprise (Java)" split is mostly history.** Banks and governments built their core
  systems in Java 15–20 years ago and never switched. Today either framework can build a large system or a quick prototype.
- **The tally (Spring 12 · ASP.NET 7) counts topics, not their weight.** One missing feature that your project needs (for example
  a built-in rate limiter, or a user-account system) can matter more than several conveniences on the other side.
- **Spring's startup gap can be mostly closed, at a cost.** With default settings Spring starts in 8.4 s and ASP.NET in
  2.1 s. Java 25's AOT cache brings Spring down to 3.2 s, but needs a training run and a 142 MB cache file.

## Limitations

- **"Better" is an opinion, built on tested facts.** The tests prove the facts behind each verdict. Which side is better is decided with the criteria in [Methodology](#how-the-winner-of-a-topic-is-decided), and someone who values them differently could pick the other side. The tally counts every topic equally.
- **One small API, without external infrastructure.** There is no real database server, message broker, identity provider or
  cloud.
- **The in-memory databases differ.** H2 has real `DECIMAL` columns and sits behind a connection pool; SQLite has no `DECIMAL`
  (EF stores money as `REAL`) and needs one connection per request. This affects the database throughput numbers.
- **Spring Boot 4.0.2 was used**, the version available offline on the used machine. A newer 4.x may exist; none of the 21 topics
  depends on a newer feature.
- **Only first-party features are compared.** Popular third-party add-ons (Bucket4j, Refit, Asp.Versioning, MediatR) are left
  out on purpose, so each side shows what its framework ships.
- **Measurements come from one machine** with default settings. The throughput rows are preliminary: measured only on Java 21,
  in single short runs, on a machine whose security layer adds about 3 ms to every localhost request.
- **The feature, developer-experience and ecosystem verdicts aren't tested or measured.** The rows in
  [Framework features](#framework-features), [Developer experience](#developer-experience) and
  [Ecosystem & community](#ecosystem--community) (message queues, Spring Batch, hot reload, scaffolding, community, job
  market) are based on public information, not on the samples. In [Runtime & performance](#runtime--performance), startup
  and memory were measured here, throughput only preliminarily; GC tuning and Native AOT were not.

## Source notes: Covered by samples

This project is based on research into many published articles that compare the two frameworks and draw conclusions.
The authors' conclusions from those articles were merged into one set of notes. The code samples set out to
cover and support those conclusions, or to correct them where they are outdated. The table below lists each point from those notes that has a topic,
and where it is shown. Points without a topic are in [Source notes: Not covered by samples](#source-notes-not-covered-by-samples).

**Added beyond the notes:**

- pagination & sorting (T17)
- declarative HTTP clients (T18)
- API versioning (T19)
- user accounts (T20)

> [!NOTE]
> When today's verdict is not the same as the one in the notes, the row gets one of these labels:
>
> - *(note outdated)*: the conclusion is no longer true; the row shows the current state.
> - *(partly outdated)*: the conclusion is still true only in part.
> - *(not confirmed)*: the samples show the opposite of what the notes said.

| From the notes | Where | Better |
|----------------|-------|:------:|
| DI power, auto-scanning, explicit registration | T02 | Spring (ASP.NET is more explicit) |
| Profiles, conditional beans | T03 | Spring |
| AOP vs middleware/filters | T09 | Spring |
| `@Transactional` vs manual transactions | T07 | Spring |
| Internal events (vs MediatR) | T08 | Spring |
| `@Scheduled` vs IHostedService/Hangfire | T10 | Spring |
| Retry: "Spring Retry" vs "only Polly" | T11 | Tie *(note outdated)*: Spring 7 has `@Retryable` in core, and .NET has first-party Microsoft.Extensions.Resilience |
| Spring Data repositories, query derivation | T05 | Spring |
| LINQ vs JPQL/Criteria | T06 | ASP.NET |
| Test slices vs WebApplicationFactory, in-memory DB | T15 (+ T05 `@DataJpaTest`) | Tie |
| Method-level security, security breadth | T12 | Spring |
| ProblemDetails (RFC 7807/9457) | T04 | ASP.NET *(partly outdated)*: Spring has had `ProblemDetail` since 6.0; ASP.NET still wins on built-in field errors |
| Rate limiting | T13 | ASP.NET |
| Built-in OpenAPI | T16 | ASP.NET |
| Health checks / Actuator | T14 | Spring (more out of the box) *(not confirmed)*: the notes said ASP.NET is simpler, but its JSON body needs a custom writer and the DB check another package |
| Minimal APIs / minimal hosting | T01, `Program.cs` | ASP.NET |
| Pattern matching in controllers | T01 | Tie *(note outdated)*: Java 21+ sealed `switch` with record patterns is as clean as C#, and exhaustive |
| Records vs Lombok | all DTOs | Neither needed *(note outdated)*: Java records replace Lombok for DTOs; this project uses no Lombok. JPA entities still need boilerplate (Domain row) |
| Async/await vs WebFlux | T01 comment | Tie *(note outdated)*: Spring MVC on virtual threads (`spring.threads.virtual.enabled`) gives simple blocking code without WebFlux |
| Response caching | T21 | ASP.NET |

## Things found while building it (both directions)

- **Spring:** test contexts are cached and shared, so tests that change data need `@DirtiesContext`, and each one starts a new context (~1 s). JPQL typos only fail at boot, not at compile time. Filters are picked up by `@WebMvcTest` slices, so the rate-limit filter reads `@Value` instead of the properties bean. `@TransactionalEventListener` doesn't fire inside a test transaction that gets rolled back (a test covers this).
- **ASP.NET:** without `launchSettings.json`, `dotnet run` starts in the **Production** environment (Spring's default profile isn't `prod`), so the first run silently picked the SMTP sender. The app reads `appsettings.json` from the *content root* (the current directory). Started from another folder, it found no config, and the startup validation (T03) correctly refused to run. A singleton that depends on a scoped service is rejected at startup, where Spring would inject a proxy. SQLite has no `DECIMAL`, so EF stores money as `REAL`; that isn't needed on SQL Server or PostgreSQL. .NET has no mocking library of its own, so services that tests replace need an interface.
- **T17–T21 findings:** Spring Data ignores repository interfaces *nested* inside another class, so `AccountRepository` has its own file. Two Spring Security filter chains (T12 demo users, T20 accounts) coexist through `securityMatcher` + `@Order`. .NET's `HttpClient.BaseAddress` needs a trailing `/`, or relative paths silently drop the last segment. HTTP Basic (T12) and Identity bearer tokens (T20) coexist through a policy scheme that picks a handler per request.
- **A real concurrency bug, found by the load test:** the first .NET version shared one in-memory `SqliteConnection` between all requests. The tests passed, because they run one request at a time, but under 32 parallel requests about 40% of them failed with HTTP 500. The fix is one connection per `DbContext` to a named shared-cache in-memory database. Spring had no such problem: H2 sits behind a connection pool automatically.
- **Startup and memory:** see [Performance: measured results](#performance-measured-results). Default settings give ≈ 8.4 s / 399 MB for Spring on Java 25 vs ≈ 2.1 s / 105 MB for ASP.NET. With Java 25's AOT cache, Spring starts in 3.2 s.

## Source notes: Not covered by samples

This project is based on research into many published articles that compare the two frameworks and draw conclusions.
The authors' conclusions from those articles were merged into one set of notes, and the code samples cover and support
as many of them as possible. This section lists the conclusions from those notes that have no topic of their own.

Some need external infrastructure (brokers, identity providers, clouds), some are runtime or tooling properties, and some are ecosystem facts. Measured numbers for the runtime rows are in [Performance: measured results](#performance-measured-results). Each row gives the current state and which side is better.

> [!NOTE]
> When a conclusion from the notes no longer holds, the "Better" column gets this label:
>
> - *(note outdated)*: the conclusion is no longer true; the row shows the current state.
>
> Words in plain brackets qualify the verdict:
>
> - (slightly): the advantage is real, but small.
> - (different styles): both are good, but they work differently, so the choice depends on the team.
> - (region-dependent): it depends on the job market where you work.

### Framework features

| From the notes | Better | Why / current state |
|-----------------------------|:------:|---------------------|
| Bean lifecycle hooks (`@PostConstruct`, `@PreDestroy`, `SmartLifecycle`) | **Spring** | Init/destroy callbacks on any bean, plus ordered start/stop phases. ASP.NET has constructors, `IDisposable`/`IAsyncDisposable` and `IHostedService` / `IHostedLifecycleService` (.NET 8), so it's workable but less uniform. |
| Enterprise integration modules (Spring Batch, Spring Integration, workflow) | **Spring** | Spring Batch gives chunked jobs, restart, skip/retry per item and job history; Spring Integration implements the Enterprise Integration Patterns. .NET has no first-party equivalents (Hangfire/Quartz.NET schedule jobs; MassTransit/NServiceBus are third-party). |
| Message queues (RabbitMQ, Kafka) | **Spring** | Spring AMQP and Spring for Apache Kafka give `@RabbitListener`/`@KafkaListener`, templates, retries and dead-letter handling in the same style as the rest of Spring. .NET uses the raw client libraries (RabbitMQ.Client, Confluent.Kafka) or third-party frameworks (MassTransit, NServiceBus; both commercial for current versions). |
| Circuit breaker | **ASP.NET** *(note outdated)* | .NET's first-party `AddStandardResilienceHandler()` gives retry + circuit breaker + timeouts on an `HttpClient` in one line. Spring's `@Retryable` (T11) doesn't include a circuit breaker; that needs Resilience4j or Spring Cloud Circuit Breaker. |
| Database migrations (Flyway/Liquibase vs EF migrations) | **Tie** (different styles) | Spring: SQL-first scripts, run automatically at startup; very strong for complex, hand-tuned changes. EF Core: migrations generated from the model (`dotnet ef migrations add`), code-first; SQL scripts and bundles are possible too. .NET also has DbUp for SQL-first. |
| Multiple datasources | **ASP.NET** *(note outdated)* | EF Core: one `AddDbContext<T>()` per database, fully independent. Spring Boot auto-configures only one `DataSource`, so a second one needs manual `DataSource` + `EntityManagerFactory` + `TransactionManager` beans with `@Qualifier`s. That's more error-prone, not less. |
| In-memory database for tests | **Spring** (slightly) | H2 is a real SQL database with `DECIMAL` and compatibility modes, used here. .NET uses SQLite in-memory, which lacks `DECIMAL` (see Domain) and needs an open connection. Both are best replaced by Testcontainers for production-like tests. |
| Security breadth (OAuth2, SAML, LDAP) | **Spring** | Spring Security has first-party OAuth2 login/client/resource server, SAML 2.0, LDAP and ACLs, plus Spring Authorization Server. ASP.NET has built-in OpenID Connect/JWT; SAML needs a third-party library and LDAP needs manual work. (User accounts are the reverse: T20.) |
| Cloud-native tooling (discovery, config server, gateway) | **Spring** | Spring Cloud: Gateway, Config Server, discovery (Eureka/Consul), Kubernetes integration. .NET has .NET Aspire (local orchestration, service discovery, telemetry) and YARP (a first-party reverse proxy), but no config-server equivalent. Not "Azure-only" any more. |
| Real-time (SignalR vs WebSocket + STOMP) | **ASP.NET** | SignalR: hubs, groups, automatic reconnect, transport fallback and client libraries (JS, .NET, Java, Swift). Spring WebSocket + STOMP works, but reconnect and client handling are lower-level. |
| Nullable safety in controllers and services | **ASP.NET** | C# nullable reference types are compiler-checked (java-vs-c# T18). Spring has JSpecify `@Nullable` annotations (Spring 7), but javac doesn't check them without extra tools like NullAway. |
| Snapshot / approval testing | **ASP.NET** (slightly) | Verify (third-party, but the de-facto standard) snapshots API responses with little setup. Java has ApprovalTests and assertj snapshot helpers, less widely used. Neither framework ships one. |
| Metrics, tracing, diagnostics | **Tie** | .NET: `System.Diagnostics.Metrics` and `ActivitySource` map directly to OpenTelemetry. Spring: Micrometer Observation + OTLP export via Actuator. Both are standards-based today. |

### Runtime & performance

| From the notes | Better | Why / current state |
|-----------------------------|:------:|---------------------|
| Raw throughput / latency | **ASP.NET** | In public benchmarks (e.g. TechEmpower) ASP.NET Core is usually near the top, ahead of Spring MVC. For typical database-bound business APIs the database dominates, so measure your own workload. |
| Startup time | **ASP.NET** | Measured here (median of 3): ≈ 8.4 s (Spring, Java 25 fat jar; 3.2 s with the AOT cache) vs ≈ 2.1 s (ASP.NET) with in-memory databases; see Performance: measured results. Spring Boot 4 improves with AOT processing and class-data sharing (CDS). |
| Memory footprint | **ASP.NET** | A .NET web app usually starts with a smaller heap and fewer loaded classes than a Spring Boot app, which matters for container density. The JVM can be tuned down, but not for free. |
| GC tuning | **ASP.NET** (slightly) | .NET's GC mostly needs no flags (Server GC; DATAS adapts heap size since .NET 8). The JVM's defaults (G1) are good, but containers and low-latency cases (ZGC, Shenandoah) often need tuning. |
| Native AOT | **ASP.NET** (slightly) | .NET Native AOT works with minimal APIs out of the box, but EF Core and reflection-based code have limits. Spring Boot supports GraalVM Native Image with AOT processing; reflection and proxies need hints, and builds are slow. Both have limits, and .NET's path is smoother for simple APIs. |

### Developer experience

| From the notes | Better | Why / current state |
|-----------------------------|:------:|---------------------|
| "Magic" auto-configuration vs explicit code | **ASP.NET** | Spring Boot auto-configuration is powerful but implicit (this project hit a hidden default: nested repositories are ignored). ASP.NET's `Program.cs` lists everything that runs. |
| Error messages when wiring fails | **ASP.NET** | .NET DI errors name the missing service and the consumer. Spring's `UnsatisfiedDependencyException` chains are long, though Boot's failure analyzers now add a short "Action:" hint. |
| Debugging configuration | **ASP.NET** | Spring needs `--debug` (the conditions report) or the Actuator `conditions` endpoint to see why a bean was or wasn't created. In ASP.NET, the registrations are the code. |
| Project structure freedom | **ASP.NET** | Spring's component scanning expects classes below the `@SpringBootApplication` package. ASP.NET doesn't care where files live. |
| Hot reload | **ASP.NET** | `dotnet watch` applies many code edits without restarting. Spring DevTools restarts the context (fast, but the app state is lost); JRebel is commercial. |
| Scaffolding | **ASP.NET** | Visual Studio / `dotnet aspnet-codegenerator` generate controllers, Identity UI and EF code. Spring has nothing built in (JHipster is third-party). |
| Project bootstrapping | **Spring** | start.spring.io (Initializr) is excellent: pick dependencies, get a ready project, also inside IntelliJ and VS Code. `dotnet new` templates are good but less granular. |
| Integrated IDE tooling | **Tie** | Visual Studio (+ Rider) for .NET vs IntelliJ IDEA (Spring tooling in the paid edition; free Spring Tools for VS Code/Eclipse). Both are strong. |
| Lombok dependency | **Neither needed** *(note outdated)* | Java records replace Lombok for DTOs, and this project uses no Lombok. JPA entities still need getters and constructors (Domain row). |
| WebFlux learning curve vs async/await | **Tie** *(note outdated)* | With virtual threads (`spring.threads.virtual.enabled`, used here), Spring MVC handles blocking code at scale without WebFlux. async/await remains the natural .NET style. |

### Ecosystem & community

| From the notes | Better | Why / current state |
|-----------------------------|:------:|---------------------|
| Ecosystem maturity, several options per problem | **Spring** | Two decades of Spring projects (Security, Batch, Integration, Data, Cloud) plus the whole Java library world. .NET often has one dominant library per problem, sometimes commercial (MediatR, MassTransit, AutoMapper went commercial in 2025). |
| Third-party / legacy enterprise integrations (SAP, mainframes, protocols) | **Spring** | More connectors and Apache Camel. .NET covers the mainstream systems well; niche enterprise protocols more often need custom code. |
| Community size, tutorials, Q&A | **Spring** | More material in total. ASP.NET's official docs (Microsoft Learn) are unusually complete, which partly makes up for it. |
| Enterprise adoption / job market | **Spring** (region-dependent) | Java + Spring is the default in banking, insurance and government in many regions; .NET is strong in Microsoft-centric companies. A market fact, not a framework property. |
