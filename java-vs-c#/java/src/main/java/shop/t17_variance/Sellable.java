// VERDICT | T17 Generic variance | BETTER: JAVA
// WHY: use-site `? extends` / `? super` works on any type; C# has no lower bound, so consumers need a delegate workaround.

package shop.t17_variance;

import java.math.BigDecimal;

public interface Sellable {

    String title();

    BigDecimal price();

    record Book(String title, BigDecimal price, int pages) implements Sellable {}

    record Ebook(String title, BigDecimal price, double sizeMb) implements Sellable {}
}
