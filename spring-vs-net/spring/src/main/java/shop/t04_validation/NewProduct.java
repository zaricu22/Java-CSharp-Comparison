// VERDICT | T04 Validation & ProblemDetails | BETTER: ASP.NET
// WHY: AddValidation() + AddProblemDetails() return RFC 9457 errors per field out of the box; Spring returns ProblemDetail too but needs an advice to list field errors.

package shop.t04_validation;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import shop.domain.Category;

import java.math.BigDecimal;

/** Bean Validation annotations on a record - checked when the controller parameter is marked @Valid. */
public record NewProduct(
        @NotBlank @Pattern(regexp = "[A-Z]\\d{1,3}", message = "must look like B12") String sku,
        @NotBlank @Size(max = 60) String name,
        @NotNull Category category,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @PositiveOrZero int stock) {
}
