// VERDICT | T21 Output caching | BETTER: ASP.NET
// WHY: OutputCache stores whole HTTP responses (vary by query, tag eviction) without touching service code; Spring has only method-level @Cacheable - the controller and JSON serialization still run.

package shop.t21_caching;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Spring's cache abstraction (in-memory here; Redis/Caffeine in production). There is no HTTP response cache. */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(CachedCatalog.CACHE);
    }
}
