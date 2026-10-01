// VERDICT | T12 Security | BETTER: SPRING
// WHY: Spring Security ships HTTP Basic, in-memory users and @PreAuthorize expressions over method arguments; ASP.NET needs a custom Basic handler and an authorization handler for the same rules.

package shop.t12_security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.t01_endpoints.ProductDto;
import shop.t07_transactions.OrderService.UnknownProductException;
import shop.t05_repositories.ProductRepository;

@Service
public class InventoryService {

    private final ProductRepository products;

    public InventoryService(ProductRepository products) {
        this.products = products;
    }

    /**
     * Method-level security with an expression over the method's own argument:
     * only admins, and never more than 1000 units at once. Enforced for every caller.
     */
    @PreAuthorize("hasRole('ADMIN') and #quantity <= 1000")
    @Transactional
    public ProductDto restock(String sku, int quantity) {
        var product = products.findById(sku).orElseThrow(() -> new UnknownProductException(sku));
        product.addStock(quantity);
        return ProductDto.from(product);
    }
}
