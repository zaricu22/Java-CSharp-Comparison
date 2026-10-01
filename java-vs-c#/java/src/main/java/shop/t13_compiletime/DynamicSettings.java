// VERDICT | T13 Compile-time features | BETTER: C#
// WHY: partial classes, source generators, #if and [Conditional]; Java has no equivalents (subclass hooks, runtime flags).

package shop.t13_compiletime;

import java.util.HashMap;
import java.util.Map;

/**
 * No dynamic type: a bag of runtime-shaped data is a Map<String, Object>
 * and every read is a string key plus a cast. (C# dynamic is rarely the right tool
 * either - it is shown for completeness, e.g. COM/JSON/scripting interop.)
 */
public final class DynamicSettings {

    private DynamicSettings() {}

    public static Map<String, Object> defaults() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("theme", "dark");
        settings.put("pageSize", 20);
        return settings;
    }

    public static int nextPageSize(Map<String, Object> settings) {
        return (Integer) settings.get("pageSize") * 2;
    }
}
