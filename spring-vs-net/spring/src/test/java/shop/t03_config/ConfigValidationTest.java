// VERDICT | T03 Configuration & profiles | BETTER: SPRING
// WHY: @Profile and @ConditionalOnProperty swap beans declaratively; ASP.NET binds options just as well, but conditional registration is hand-written if/else.

package shop.t03_config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/** ApplicationContextRunner: starts a tiny context in milliseconds - just the properties binding. */
class ConfigValidationTest {

    @EnableConfigurationProperties(ShopProperties.class)
    static class PropertiesOnly {
    }

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(PropertiesOnly.class)
            .withPropertyValues("shop.free-shipping-over=50", "shop.features.recommendations=false",
                    "shop.jobs.cleanup-rate=PT1H", "shop.rate-limit.permits=3", "shop.rate-limit.window=PT10S");

    @Test
    void validSettingsBind() {
        runner.withPropertyValues("shop.currency=EUR").run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context.getBean(ShopProperties.class).currency()).isEqualTo("EUR");
        });
    }

    @Test
    void invalidSettingsStopTheApplication() {
        runner.withPropertyValues("shop.currency=").run(context -> assertThat(context).hasFailed());
    }
}
