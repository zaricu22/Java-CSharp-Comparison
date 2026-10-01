// VERDICT | Application entry point | BETTER: -
// WHY: component scanning finds every @Component/@Service/@RestController below this package; the annotations switch on scheduling, retries and config binding.

package shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan   // T03: binds ShopProperties
@EnableScheduling              // T10: @Scheduled
@EnableResilientMethods        // T11: @Retryable
public class ShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }
}
