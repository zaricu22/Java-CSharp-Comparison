// VERDICT | T20 Expression trees | BETTER: C#
// WHY: a plain lambda can be inspected and translated to SQL (EF Core); Java lambdas are opaque, so you need a Criteria/metamodel DSL.

package shop.t20_expressions;

import shop.domain.Customer;
import shop.domain.Product;
import shop.t20_expressions.Criteria.Condition;
import shop.t20_expressions.Criteria.Field;

import java.math.BigDecimal;
import java.util.List;

/** The hand-written metamodel (JPA would generate Product_ / Customer_ classes for this). */
public final class Queries {

    private Queries() {}

    public static final Field<Product, BigDecimal> PRODUCT_PRICE = new Field<>("Price", Product::price);
    public static final Field<Product, String> PRODUCT_NAME = new Field<>("Name", Product::name);
    public static final Field<Customer, String> CUSTOMER_CITY = new Field<>("City", Customer::city);
    public static final Field<Customer, Integer> CUSTOMER_ID = new Field<>("Id", Customer::id);

    public static String where(String table, Condition<?> condition) {
        return "SELECT * FROM " + table + " WHERE " + condition.sql();
    }

    public static <T> List<T> filter(List<T> items, Condition<T> condition) {
        return items.stream().filter(condition.test()).toList();
    }
}
