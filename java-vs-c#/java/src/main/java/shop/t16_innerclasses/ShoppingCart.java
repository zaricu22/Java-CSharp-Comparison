// VERDICT | T16 Anonymous & inner classes | BETTER: JAVA
// WHY: anonymous classes implement multi-method interfaces inline and inner classes see the outer instance; C# needs named classes.

package shop.t16_innerclasses;

import shop.domain.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public final class ShoppingCart {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final List<Item> items = new ArrayList<>();

    /**
     * Where Java wins: an inner (non-static) class holds an implicit reference to its
     * enclosing instance. Item calls subtotal() and touches items directly, no wiring needed.
     */
    public class Item {

        private final Product product;
        private int quantity;

        private Item(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product product() {
            return product;
        }

        public int quantity() {
            return quantity;
        }

        public BigDecimal lineTotal() {
            return product.price().multiply(BigDecimal.valueOf(quantity));
        }

        /** Percentage of the whole cart - reads the outer instance implicitly. */
        public BigDecimal shareOfCart() {
            return lineTotal().multiply(HUNDRED).divide(subtotal(), 1, RoundingMode.HALF_EVEN);
        }

        public void increase() {
            quantity++;
        }

        public void remove() {
            items.remove(this);
        }
    }

    public Item add(Product product, int quantity) {
        Item item = new Item(product, quantity);
        items.add(item);
        return item;
    }

    public BigDecimal subtotal() {
        return items.stream().map(Item::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int size() {
        return items.size();
    }
}
