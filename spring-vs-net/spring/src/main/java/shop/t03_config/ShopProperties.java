// VERDICT | T03 Configuration & profiles | BETTER: SPRING
// WHY: @Profile and @ConditionalOnProperty swap beans declaratively; ASP.NET binds options just as well, but conditional registration is hand-written if/else.

package shop.t03_config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.Duration;

/**
 * Typed, validated settings bound from the "shop" section of application.yml
 * (and overridden by application-{profile}.yml). Invalid values stop the app at startup.
 */
@Validated
@ConfigurationProperties(prefix = "shop")
public record ShopProperties(
        @NotBlank String currency,
        @NotNull @DecimalMin("0") BigDecimal freeShippingOver,
        @Valid @NotNull Features features,
        @Valid @NotNull Jobs jobs,
        @Valid @NotNull RateLimit rateLimit) {

    public record Features(boolean recommendations) {}

    public record Jobs(@NotNull Duration cleanupRate) {}

    public record RateLimit(int permits, @NotNull Duration window) {}
}
