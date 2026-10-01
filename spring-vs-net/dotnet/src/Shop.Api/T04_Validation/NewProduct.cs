// VERDICT | T04 Validation & ProblemDetails | BETTER: ASP.NET
// WHY: AddValidation() + AddProblemDetails() return RFC 9457 errors per field out of the box; Spring returns ProblemDetail too but needs an advice to list field errors.

using System.ComponentModel.DataAnnotations;

namespace Shop.Api.T04_Validation;

/// <summary>
/// DataAnnotations on a record. With builder.Services.AddValidation() (.NET 10) minimal APIs validate
/// it automatically and answer 400 with a ValidationProblemDetails listing every field - no extra code.
/// </summary>
public record NewProduct(
    [Required, RegularExpression(@"^[A-Z]\d{1,3}$", ErrorMessage = "must look like B12")] string Sku,
    [Required, StringLength(60)] string Name,
    [Required] Category? Category,
    [Range(typeof(decimal), "0.01", "1000000")] decimal Price,
    [Range(0, int.MaxValue)] int Stock);
