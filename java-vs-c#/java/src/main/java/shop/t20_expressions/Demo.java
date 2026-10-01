// VERDICT | T20 Expression trees | BETTER: C#
// WHY: a plain lambda can be inspected and translated to SQL (EF Core); Java lambdas are opaque, so you need a Criteria/metamodel DSL.

package shop.t20_expressions;

import shop.domain.SampleData;

import java.math.BigDecimal;

import static shop.t20_expressions.Queries.CUSTOMER_CITY;
import static shop.t20_expressions.Queries.CUSTOMER_ID;
import static shop.t20_expressions.Queries.PRODUCT_NAME;
import static shop.t20_expressions.Queries.PRODUCT_PRICE;

public final class Demo {

    public static void run() {
        var minPrice = new BigDecimal("40");
        // Instead of a lambda: a condition built from the metamodel DSL
        var expensiveKeyboards = PRODUCT_PRICE.gt(minPrice).and(Criteria.startsWith(PRODUCT_NAME, "Key"));
        System.out.println(Queries.where("Products", expensiveKeyboards));
        System.out.println("In memory: " + Queries.filter(SampleData.products(), expensiveKeyboards).stream()
                .map(p -> p.name()).toList());

        System.out.println(Queries.where("Customers", CUSTOMER_CITY.eq("Belgrade").or(CUSTOMER_ID.le(2))));
    }
}
