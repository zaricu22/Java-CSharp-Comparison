// VERDICT | T21 Output caching | BETTER: ASP.NET
// WHY: OutputCache stores whole HTTP responses (vary by query, tag eviction) without touching service code; Spring has only method-level @Cacheable - the controller and JSON serialization still run.

package shop.t21_caching;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.domain.Category;
import shop.t01_endpoints.ProductDto;
import shop.t05_repositories.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The closest built-in Spring gets: cache the METHOD result. Every request still goes through the
 * controller and JSON serialization; the cache key is written by hand as an expression, and varying
 * by header, user or culture means more key code.
 */
@Service
public class CachedCatalog {

    static final String CACHE = "products";

    private final ProductRepository products;
    private final AtomicInteger databaseQueries = new AtomicInteger();

    public CachedCatalog(ProductRepository products) {
        this.products = products;
    }

    @Cacheable(cacheNames = CACHE, key = "#category == null ? 'all' : #category.name()")
    @Transactional(readOnly = true)
    public List<ProductDto> list(Category category) {
        databaseQueries.incrementAndGet();
        var entities = category == null ? products.findAll() : products.findByCategoryOrderByPriceAsc(category);
        return entities.stream().map(ProductDto::from).toList();
    }

    @CacheEvict(cacheNames = CACHE, allEntries = true)
    @Transactional
    public Optional<ProductDto> changePrice(String sku, BigDecimal price) {
        return products.findById(sku).map(product -> {
            product.changePrice(price);
            return ProductDto.from(product);
        });
    }

    public int databaseQueries() {
        return databaseQueries.get();
    }
}
