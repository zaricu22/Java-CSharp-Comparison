// VERDICT | T03 Type system & generics | BETTER: C#
// WHY: reified generics (typeof(T), new T(), List<int> without boxing) and generic math; Java has erasure and Integer boxing traps.

package shop.t03_types;

import java.util.List;
import java.util.function.BinaryOperator;

public final class TypeSystem {

    private TypeSystem() {}

    /** Primitives are not objects: {@code quantity.toString()} does not compile, use wrapper statics. */
    public static String describeStock(int quantity) {
        return Integer.toString(quantity) + " units (0x" + Integer.toHexString(quantity) + ")";
    }

    /** {@code List<int>} is illegal - every element is a boxed Integer (a heap object that can be null). */
    public static int totalUnits(List<Integer> quantities) {
        int sum = 0;
        for (int q : quantities) { // auto-unboxing: NullPointerException on a null element
            sum += q;
        }
        return sum;
    }

    /**
     * No generic math: one method for all numeric types needs the caller to pass
     * "zero" and "plus" explicitly (Integer, Long, BigDecimal share no arithmetic interface).
     */
    public static <T> T sum(List<T> values, T zero, BinaryOperator<T> add) {
        T total = zero;
        for (T v : values) {
            total = add.apply(total, v);
        }
        return total;
    }

    /**
     * Type erasure: {@code o instanceof List<String>} does not compile, because at runtime
     * every List is just List. The only option is to inspect elements, which is wrong for empty lists.
     */
    public static boolean isListOfStrings(Object o) {
        return o instanceof List<?> list && list.stream().allMatch(e -> e instanceof String);
    }
}
