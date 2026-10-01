// VERDICT | T13 Compile-time features | BETTER: C#
// WHY: partial classes, source generators, #if and [Conditional]; Java has no equivalents (subclass hooks, runtime flags).

package shop.t13_compiletime;

public final class Demo {

    public static void run() {
        System.out.println("B12 valid? " + SkuValidator.isValid("B12") + ", b-12 valid? " + SkuValidator.isValid("b-12"));
        System.out.println("Empty form errors: " + new ProductForm().validate());
        System.out.println("Build mode: " + BuildInfo.mode());
        BuildInfo.trace(() -> "demo 13 ran");
        System.out.println("Trace: " + BuildInfo.traced());

        var settings = DynamicSettings.defaults();
        System.out.println("Theme " + settings.get("theme") + ", next page size " + DynamicSettings.nextPageSize(settings));
    }
}
