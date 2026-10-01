// VERDICT | T11 Indexers & ranges | BETTER: C#
// WHY: catalog["E1"], matrix[1, 1], items[1..^1]; Java spells everything as get()/subList()/substring().

package shop.t11_indexers;

import java.util.List;

/** No index-from-end / range syntax: arithmetic with size() and subList/substring. */
public final class Ranges {

    private Ranges() {}

    public static <T> List<T> lastN(List<T> items, int n) {
        return items.subList(Math.max(0, items.size() - n), items.size());
    }

    public static <T> List<T> withoutFirstAndLast(List<T> items) {
        return items.subList(1, items.size() - 1);
    }

    public static String skuPrefix(String sku) {
        return sku.substring(0, 1);
    }

    public static String lastChars(String s, int n) {
        return s.substring(s.length() - n);
    }
}
