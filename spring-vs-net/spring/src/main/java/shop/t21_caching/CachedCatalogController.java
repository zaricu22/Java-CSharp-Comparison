// VERDICT | T21 Output caching | BETTER: ASP.NET
// WHY: OutputCache stores whole HTTP responses (vary by query, tag eviction) without touching service code; Spring has only method-level @Cacheable - the controller and JSON serialization still run.

package shop.t21_caching;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.domain.Category;
import shop.t01_endpoints.ProductDto;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class CachedCatalogController {

    private final CachedCatalog catalog;

    public CachedCatalogController(CachedCatalog catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/api/cached/products")
    public List<ProductDto> list(@RequestParam(required = false) Category category) {
        return catalog.list(category);
    }

    @PutMapping("/api/cached/products/{sku}/price")
    public ResponseEntity<Void> changePrice(@PathVariable String sku, @RequestParam BigDecimal price) {
        return catalog.changePrice(sku, price).isPresent()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
