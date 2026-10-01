// VERDICT | T01 Endpoints & hosting | BETTER: ASP.NET
// WHY: minimal APIs map a route to a lambda with typed results in one line; Spring needs a @RestController class (Java 21 sealed switch maps results as cleanly as C#).

package shop.t01_endpoints;

import shop.domain.Category;
import shop.domain.ProductEntity;

import java.math.BigDecimal;

public record ProductDto(String sku, String name, Category category, BigDecimal price, int stock) {

    public static ProductDto from(ProductEntity p) {
        return new ProductDto(p.getSku(), p.getName(), p.getCategory(), p.getPrice(), p.getStock());
    }
}
