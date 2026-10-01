// VERDICT | T11 Indexers & ranges | BETTER: C#
// WHY: catalog["E1"], matrix[1, 1], items[1..^1]; Java spells everything as get()/subList()/substring().

package shop.t11_indexers;

import shop.domain.Category;
import shop.domain.SampleData;

import java.util.List;

public final class Demo {

    public static void run() {
        var catalog = new Catalog(SampleData.products());
        System.out.println("catalog.get(\"E1\") = " + catalog.get("E1").name());
        System.out.println("Books: " + catalog.get(Category.BOOKS).stream().map(p -> p.name()).toList());

        var matrix = new ShippingMatrix();
        System.out.println("EU, medium parcel: " + matrix.get(1, 1));

        var skus = List.of("B1", "B2", "E1", "E2", "G1");
        System.out.println("Last two: " + Ranges.lastN(skus, 2) + ", middle: " + Ranges.withoutFirstAndLast(skus));
    }
}
