// VERDICT | T03 Type system & generics | BETTER: C#
// WHY: reified generics (typeof(T), new T(), List<int> without boxing) and generic math; Java has erasure and Integer boxing traps.

package shop.t03_types;

import java.math.BigDecimal;
import java.util.List;

public final class Demo {

    public static void run() {
        System.out.println(TypeSystem.describeStock(255));
        System.out.println("Units: " + TypeSystem.totalUnits(List.of(2, 1, 3)));
        System.out.println("Sum ints: " + TypeSystem.sum(List.of(1, 2, 3), 0, Integer::sum));
        System.out.println("Sum money: " + TypeSystem.sum(
                List.of(new BigDecimal("1.10"), new BigDecimal("2.20")), BigDecimal.ZERO, BigDecimal::add));

        var drafts = new Repository<>(CartDraft.class, CartDraft::new);
        drafts.createNew();
        System.out.println(drafts.entityName() + " count=" + drafts.count());

        List<Integer> emptyInts = List.of();
        System.out.println("Empty List<Integer> is List<String>? " + TypeSystem.isListOfStrings(emptyInts));
    }
}
