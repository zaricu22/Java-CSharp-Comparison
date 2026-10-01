// VERDICT | T13 Compile-time features | BETTER: C#
// WHY: partial classes, source generators, #if and [Conditional]; Java has no equivalents (subclass hooks, runtime flags).

package shop.t13_compiletime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Imagine a code generator produced this file. Java has no partial classes, so hand-written
 * code must go into a subclass (or a separate helper) and the "hook" has to be a
 * protected method that is always called - even when nobody overrides it.
 */
public abstract class ProductFormGenerated {

    private String name = "";
    private BigDecimal price = BigDecimal.ZERO;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (name.isBlank()) {
            errors.add("Name is required");
        }
        onValidating(errors);
        return errors;
    }

    protected void onValidating(List<String> errors) {
    }
}
