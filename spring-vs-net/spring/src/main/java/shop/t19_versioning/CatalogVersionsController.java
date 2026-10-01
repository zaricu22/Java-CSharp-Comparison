// VERDICT | T19 API versioning | BETTER: SPRING
// WHY: Spring 7 routes by version (header/path/query/media type), rejects unsupported versions and sends Deprecation/Sunset headers; ASP.NET needs the third-party Asp.Versioning package or hand-written branching.

package shop.t19_versioning;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import shop.t01_endpoints.CatalogService;
import shop.t01_endpoints.ProductDto;
import shop.t03_config.ShopProperties;

import java.math.BigDecimal;

/** Two versions of one resource: one method per version, selected by the framework. */
@RestController
@RequestMapping("/api/catalog/{sku}")
public class CatalogVersionsController {

    public record ProductV1(String sku, String name, BigDecimal price) {}

    public record ProductV2(String sku, String name, Money price, boolean inStock) {
        public record Money(BigDecimal amount, String currency) {}
    }

    private final CatalogService catalog;
    private final ShopProperties properties;

    public CatalogVersionsController(CatalogService catalog, ShopProperties properties) {
        this.catalog = catalog;
        this.properties = properties;
    }

    @GetMapping(version = "1")
    public ProductV1 v1(@PathVariable String sku) {
        var p = find(sku);
        return new ProductV1(p.sku(), p.name(), p.price());
    }

    @GetMapping(version = "2")
    public ProductV2 v2(@PathVariable String sku) {
        var p = find(sku);
        return new ProductV2(p.sku(), p.name(), new ProductV2.Money(p.price(), properties.currency()), p.stock() > 0);
    }

    private ProductDto find(String sku) {
        return catalog.find(sku).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product " + sku + " not found"));
    }
}
