// VERDICT | T16 OpenAPI | BETTER: ASP.NET
// WHY: AddOpenApi()/MapOpenApi() are first-party; Spring relies on the third-party springdoc project, which has to track every Boot release.

package shop.t16_openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** springdoc scans the controllers and serves the document at /v3/api-docs. */
@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI shopApi() {
        return new OpenAPI().info(new Info().title("Shop API").version("v1"));
    }
}
