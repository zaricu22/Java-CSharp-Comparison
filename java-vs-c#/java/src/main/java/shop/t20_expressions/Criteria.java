// VERDICT | T20 Expression trees | BETTER: C#
// WHY: a plain lambda can be inspected and translated to SQL (EF Core); Java lambdas are opaque, so you need a Criteria/metamodel DSL.

package shop.t20_expressions;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Java lambdas are opaque code: a library cannot look inside {@code p -> p.price() > 40}.
 * To get both SQL and in-memory filtering from one definition you need a hand-made
 * query DSL with a "metamodel" of columns. This is what JPA Criteria, QueryDSL and
 * jOOQ provide, usually through generated classes.
 */
public final class Criteria {

    private Criteria() {}

    /** A condition that knows its SQL and how to test an object. */
    public record Condition<T>(String sql, Predicate<T> test) {

        public Condition<T> and(Condition<T> other) {
            return new Condition<>("(" + sql + " AND " + other.sql + ")", test.and(other.test));
        }

        public Condition<T> or(Condition<T> other) {
            return new Condition<>("(" + sql + " OR " + other.sql + ")", test.or(other.test));
        }
    }

    /** A typed column: its SQL name plus the getter used in memory. */
    public record Field<T, V extends Comparable<V>>(String column, Function<T, V> getter) {

        public Condition<T> eq(V value) {
            return compare("=", value, c -> c == 0);
        }

        public Condition<T> gt(V value) {
            return compare(">", value, c -> c > 0);
        }

        public Condition<T> le(V value) {
            return compare("<=", value, c -> c <= 0);
        }

        private Condition<T> compare(String op, V value, Predicate<Integer> check) {
            return new Condition<>("(" + column + " " + op + " " + literal(value) + ")",
                    item -> check.test(getter.apply(item).compareTo(value)));
        }
    }

    public static <T> Condition<T> startsWith(Field<T, String> field, String prefix) {
        return new Condition<>(field.column() + " LIKE " + literal(prefix + "%"),
                item -> field.getter().apply(item).startsWith(prefix));
    }

    public static String literal(Object value) {
        if (value == null) {
            return "NULL";
        }
        if (value instanceof String s) {
            return "'" + s.replace("'", "''") + "'";
        }
        if (value instanceof java.math.BigDecimal d) {
            return d.toPlainString();
        }
        return value.toString();
    }
}
