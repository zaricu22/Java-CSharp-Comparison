// VERDICT | T13 Compile-time features | BETTER: C#
// WHY: partial classes, source generators, #if and [Conditional]; Java has no equivalents (subclass hooks, runtime flags).

package shop.t13_compiletime;

import java.math.BigDecimal;
import java.util.List;

/** The hand-written half: has to extend the generated class. */
public class ProductForm extends ProductFormGenerated {

    @Override
    protected void onValidating(List<String> errors) {
        if (getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Price must be positive");
        }
    }
}
