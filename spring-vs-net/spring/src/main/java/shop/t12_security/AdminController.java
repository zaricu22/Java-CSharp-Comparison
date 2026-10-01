// VERDICT | T12 Security | BETTER: SPRING
// WHY: Spring Security ships HTTP Basic, in-memory users and @PreAuthorize expressions over method arguments; ASP.NET needs a custom Basic handler and an authorization handler for the same rules.

package shop.t12_security;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.t01_endpoints.ProductDto;

@RestController
public class AdminController {

    private final InventoryService inventory;

    public AdminController(InventoryService inventory) {
        this.inventory = inventory;
    }

    @PostMapping("/api/admin/products/{sku}/restock")
    public ProductDto restock(@PathVariable String sku, @RequestParam int quantity) {
        return inventory.restock(sku, quantity);
    }
}
