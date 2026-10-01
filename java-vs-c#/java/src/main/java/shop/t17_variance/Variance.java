// VERDICT | T17 Generic variance | BETTER: JAVA
// WHY: use-site `? extends` / `? super` works on any type; C# has no lower bound, so consumers need a delegate workaround.

package shop.t17_variance;

import shop.t17_variance.Sellable.Ebook;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Where Java wins: use-site variance. Any method can say "a list of something that
 * extends X" (producer) or "a list of something that is a supertype of X" (consumer),
 * on any generic type, including mutable ones like List. C# only has declaration-site
 * variance (in/out on interfaces and delegates) and no lower-bounded ("super") constraint.
 */
public final class Variance {

    private Variance() {}

    /** Producer: accepts List<Book>, List<Ebook>, List<Sellable>. */
    public static BigDecimal total(List<? extends Sellable> items) {
        return items.stream().map(Sellable::price).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Consumer: can add an Ebook into List<Ebook>, List<Sellable> or List<Object>. */
    public static void addFreebie(List<? super Ebook> target) {
        target.add(new Ebook("Java Cheat Sheet", BigDecimal.ZERO, 1.2));
    }

    /** PECS: "producer extends, consumer super" in one signature. */
    public static <T> void copyMatching(List<? extends T> source, List<? super T> target, Predicate<? super T> filter) {
        for (T item : source) {
            if (filter.test(item)) {
                target.add(item);
            }
        }
    }

    public static <T> T cheapest(List<? extends T> items, Comparator<? super T> byPrice) {
        return items.stream().min(byPrice).orElseThrow();
    }
}
