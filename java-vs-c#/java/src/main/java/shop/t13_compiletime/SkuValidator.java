// VERDICT | T13 Compile-time features | BETTER: C#
// WHY: partial classes, source generators, #if and [Conditional]; Java has no equivalents (subclass hooks, runtime flags).

package shop.t13_compiletime;

import java.util.regex.Pattern;

/**
 * The regex is parsed and compiled at runtime (class load). C# can generate the matcher at
 * compile time with a source generator plugged into a partial method; Java has no partial
 * classes, so generated code must live in a separate class you extend or delegate to.
 */
public final class SkuValidator {

    private static final Pattern SKU = Pattern.compile("^[A-Z]\\d{1,3}$");

    private SkuValidator() {}

    public static boolean isValid(String sku) {
        return SKU.matcher(sku).matches();
    }
}
