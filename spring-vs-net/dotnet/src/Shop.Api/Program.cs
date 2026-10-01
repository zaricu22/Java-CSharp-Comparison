// VERDICT | Application entry point | BETTER: -
// WHY: the whole application is wired here explicitly - every service, middleware and endpoint group is listed (Spring finds them by scanning).

using System.Text.Json.Serialization;
using Shop.Api.T01_Endpoints;
using Shop.Api.T02_Di;
using Shop.Api.T03_Config;
using Shop.Api.T05_Repositories;
using Shop.Api.T06_Queries;
using Shop.Api.T07_Transactions;
using Shop.Api.T08_Events;
using Shop.Api.T09_Aop;
using Shop.Api.T10_Scheduling;
using Shop.Api.T11_Resilience;
using Shop.Api.T12_Security;
using Shop.Api.T13_RateLimiting;
using Shop.Api.T14_Health;
using Shop.Api.T16_OpenApi;
using Shop.Api.T17_Pagination;
using Shop.Api.T18_HttpClients;
using Shop.Api.T19_Versioning;
using Shop.Api.T20_Accounts;
using Shop.Api.T21_Caching;

var builder = WebApplication.CreateBuilder(args);

builder.Services.ConfigureHttpJsonOptions(json => json.SerializerOptions.Converters.Add(new JsonStringEnumConverter()));
builder.Services.AddProblemDetails();   // T04: RFC 9457 bodies for errors
builder.Services.AddValidation();       // T04: DataAnnotations on minimal API parameters

builder.AddShopConfiguration();          // T03
builder.Services.AddShopPersistence();   // T05 (EF Core + in-memory SQLite)
builder.Services.AddCatalog();           // T01
builder.Services.AddPayments();          // T02
builder.Services.AddOrders();            // T07
builder.Services.AddShopEvents();        // T08
builder.Services.AddAuditing();          // T09
builder.Services.AddJobs();              // T10
builder.Services.AddRates();             // T11
builder.Services.AddShopSecurity();      // T12
builder.Services.AddSearchRateLimiting();// T13
builder.Services.AddShopHealthChecks();  // T14
builder.Services.AddShopOpenApi();       // T16
builder.Services.AddSupplierClient(builder.Configuration); // T18
builder.Services.AddUserAccounts();      // T20 (after T12: sets the default auth scheme)
builder.Services.AddShopOutputCaching(); // T21

var app = builder.Build();

app.UseExceptionHandler();
app.UseStatusCodePages();
app.UseAuthentication();
app.UseAuthorization();
app.UseRateLimiter();
app.UseOutputCache();       // T21

await app.SeedAsync();

app.MapProductEndpoints();    // T01
app.MapDiEndpoints();         // T02
app.MapConfigEndpoints();     // T03
app.MapInventoryEndpoints();  // T05
app.MapReportEndpoints();     // T06
app.MapOrderEndpoints();      // T07
app.MapRateEndpoints();       // T11
app.MapAdminEndpoints();      // T12
app.MapSearchEndpoints();     // T13
app.MapShopHealthChecks();    // T14
app.MapOpenApi();             // T16
app.MapPagedProducts();       // T17
app.MapVersionedCatalog();    // T19
app.MapAccountEndpoints();    // T20
app.MapCachedCatalog();       // T21

app.Run();

/// <summary>Makes the entry point visible to WebApplicationFactory&lt;Program&gt; in the tests.</summary>
public partial class Program;
