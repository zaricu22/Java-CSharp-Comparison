// VERDICT | T05 Repositories | BETTER: SPRING
// WHY: Spring Data derives queries from method names with zero implementation; EF's DbSet is already a repository, but every query is written as LINQ.

package shop.t05_repositories;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.t01_endpoints.ProductDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
public class InventoryController {

    private final ProductRepository products;

    public InventoryController(ProductRepository products) {
        this.products = products;
    }

    /** In-stock products cheaper than maxPrice, cheapest first. */
    @GetMapping("/api/inventory/affordable")
    @Transactional(readOnly = true)
    public List<ProductDto> affordable(@RequestParam BigDecimal maxPrice) {
        return products.findByPriceLessThanAndStockGreaterThanOrderByPriceAsc(maxPrice, 0).stream()
                .map(ProductDto::from).toList();
    }

    @GetMapping("/api/inventory/low-stock-count")
    public Map<String, Long> lowStockCount(@RequestParam(defaultValue = "5") int threshold) {
        return Map.of("count", products.countByStockLessThan(threshold));
    }
}
