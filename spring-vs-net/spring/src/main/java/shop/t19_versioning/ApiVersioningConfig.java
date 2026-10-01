// VERDICT | T19 API versioning | BETTER: SPRING
// WHY: Spring 7 routes by version (header/path/query/media type), rejects unsupported versions and sends Deprecation/Sunset headers; ASP.NET needs the third-party Asp.Versioning package or hand-written branching.

package shop.t19_versioning;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.accept.StandardApiVersionDeprecationHandler;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Built into Spring Framework 7: the version comes from the X-API-Version header (it could also be
 * a path segment, query parameter or media type parameter), versions declared on mappings are the
 * supported ones, anything else is a 400, and v1 answers carry RFC 9745 Deprecation + RFC 8594 Sunset headers.
 */
@Configuration
public class ApiVersioningConfig implements WebMvcConfigurer {

    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        var deprecation = new StandardApiVersionDeprecationHandler();
        deprecation.configureVersion("1")
                .setRequestPredicate(request -> request.getRequestURI().startsWith("/api/catalog"))
                .setDeprecationDate(ZonedDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .setSunsetDate(ZonedDateTime.of(2026, 12, 31, 0, 0, 0, 0, ZoneOffset.UTC));

        configurer.useRequestHeader("X-API-Version")
                .setDefaultVersion("1")   // clients that send no header keep getting v1
                .setDeprecationHandler(deprecation);
    }
}
