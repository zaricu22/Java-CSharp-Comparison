// VERDICT | T20 Expression trees | BETTER: C#
// WHY: a plain lambda can be inspected and translated to SQL (EF Core); Java lambdas are opaque, so you need a Criteria/metamodel DSL.

package shop.t20_expressions;

import org.junit.jupiter.api.Test;
import shop.domain.Product;
import shop.domain.SampleData;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static shop.t20_expressions.Queries.CUSTOMER_CITY;
import static shop.t20_expressions.Queries.CUSTOMER_ID;
import static shop.t20_expressions.Queries.PRODUCT_NAME;
import static shop.t20_expressions.Queries.PRODUCT_PRICE;

class ExpressionsTest {

    private final Criteria.Condition<Product> expensiveKeyboards =
            PRODUCT_PRICE.gt(new BigDecimal("40")).and(Criteria.startsWith(PRODUCT_NAME, "Key"));

    @Test
    void conditionTranslatesToSql() {
        assertEquals("SELECT * FROM Products WHERE ((Price > 40) AND Name LIKE 'Key%')",
                Queries.where("Products", expensiveKeyboards));
        assertEquals("SELECT * FROM Customers WHERE ((City = 'Belgrade') OR (Id <= 2))",
                Queries.where("Customers", CUSTOMER_CITY.eq("Belgrade").or(CUSTOMER_ID.le(2))));
    }

    @Test
    void sameConditionFiltersInMemory() {
        assertEquals(List.of("Keyboard"),
                Queries.filter(SampleData.products(), expensiveKeyboards).stream().map(Product::name).toList());
    }

    @Test
    void stringLiteralsAreEscaped() {
        assertEquals("SELECT * FROM Customers WHERE (City = 'O''Hara')", Queries.where("Customers", CUSTOMER_CITY.eq("O'Hara")));
    }
}
