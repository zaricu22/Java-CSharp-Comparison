// VERDICT | T13 Rate limiting | BETTER: ASP.NET
// WHY: AddRateLimiter + RequireRateLimiting are built in and partitioned per client; Spring has no rate limiter, so it is a hand-written filter (or Bucket4j).

package shop.t13_ratelimiting;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.t01_endpoints.CatalogService;
import shop.t01_endpoints.ProductDto;

import java.util.List;

@RestController
public class SearchController {

    private final CatalogService catalog;

    public SearchController(CatalogService catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/api/search")
    public List<ProductDto> search(@RequestParam String q) {
        return catalog.search(q);
    }
}
