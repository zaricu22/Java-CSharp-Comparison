// VERDICT | Demo runner | BETTER: -
// WHY: runs every topic's Demo in order; pass a topic number (e.g. 04) to run just one.

package shop;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class Main {

    @FunctionalInterface
    interface DemoRunner {
        void run() throws Exception;
    }

    // Java 25 (JEP 512): an instance main method - no "public static" needed
    void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US); // same number formatting as the C# side (InvariantGlobalization)

        Map<String, DemoRunner> demos = new LinkedHashMap<>();
        demos.put("01 Properties & records", shop.t01_properties.Demo::run);
        demos.put("02 Strings", shop.t02_strings.Demo::run);
        demos.put("03 Type system & generics", shop.t03_types.Demo::run);
        demos.put("04 LINQ vs Streams", shop.t04_queries.Demo::run);
        demos.put("05 Operators & value types", shop.t05_operators.Demo::run);
        demos.put("06 Extension methods", shop.t06_extensions.Demo::run);
        demos.put("07 Events & functions", shop.t07_events.Demo::run);
        demos.put("08 Async", shop.t08_async.Demo::run);
        demos.put("09 Pattern matching", shop.t09_patterns.Demo::run);
        demos.put("10 Method parameters", shop.t10_parameters.Demo::run);
        demos.put("11 Indexers & ranges", shop.t11_indexers.Demo::run);
        demos.put("12 Exception filters & overflow", shop.t12_errors.Demo::run);
        demos.put("13 Compile-time features", shop.t13_compiletime.Demo::run);
        demos.put("14 Checked exceptions", shop.t14_checked.Demo::run);
        demos.put("15 Enums", shop.t15_enums.Demo::run);
        demos.put("16 Anonymous & inner classes", shop.t16_innerclasses.Demo::run);
        demos.put("17 Generic variance", shop.t17_variance.Demo::run);
        demos.put("18 Null safety", shop.t18_nullsafety.Demo::run);
        demos.put("19 Iterators & generators", shop.t19_iterators.Demo::run);
        demos.put("20 Expression trees", shop.t20_expressions.Demo::run);
        demos.put("21 Memory & value types", shop.t21_memory.Demo::run);
        demos.put("22 Resource cleanup", shop.t22_resources.Demo::run);
        demos.put("23 Collection expressions", shop.t23_collections.Demo::run);
        demos.put("24 Labeled break & continue", shop.t24_loops.Demo::run);

        String only = args.length > 0 ? args[0] : null;
        for (var demo : demos.entrySet()) {
            if (only != null && !demo.getKey().startsWith(only)) {
                continue;
            }
            System.out.println("== " + demo.getKey() + " ==");
            demo.getValue().run();
            System.out.println();
        }
    }
}
