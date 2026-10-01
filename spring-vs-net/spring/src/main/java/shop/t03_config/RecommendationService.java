// VERDICT | T03 Configuration & profiles | BETTER: SPRING
// WHY: @Profile and @ConditionalOnProperty swap beans declaratively; ASP.NET binds options just as well, but conditional registration is hand-written if/else.

package shop.t03_config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.t05_repositories.ProductRepository;

import java.util.List;

/** This bean only exists when shop.features.recommendations=true (a feature flag). */
@Service
@ConditionalOnProperty(prefix = "shop.features", name = "recommendations", havingValue = "true")
public class RecommendationService {

    private final ProductRepository products;

    public RecommendationService(ProductRepository products) {
        this.products = products;
    }

    /** Other products from the same category. */
    @Transactional(readOnly = true)
    public List<String> recommendFor(String sku) {
        return products.findById(sku)
                .map(p -> products.findByCategoryOrderByPriceAsc(p.getCategory()).stream()
                        .filter(other -> !other.getSku().equals(sku))
                        .map(other -> other.getName())
                        .toList())
                .orElse(List.of());
    }
}
