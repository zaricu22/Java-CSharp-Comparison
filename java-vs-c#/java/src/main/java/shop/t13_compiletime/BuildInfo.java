// VERDICT | T13 Compile-time features | BETTER: C#
// WHY: partial classes, source generators, #if and [Conditional]; Java has no equivalents (subclass hooks, runtime flags).

package shop.t13_compiletime;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * No preprocessor and no conditional methods. The closest things:
 *  - a compile-time constant: javac drops {@code if (DEBUG)} blocks when it is false,
 *    but switching it means editing source, not choosing a build configuration;
 *  - passing a Supplier so the (expensive) message is not built when tracing is off -
 *    the call itself always remains.
 */
public final class BuildInfo {

    public static final boolean DEBUG = true;

    private static final List<String> TRACE = new ArrayList<>();

    private BuildInfo() {}

    public static String mode() {
        if (DEBUG) {
            return "Debug";
        }
        return "Release";
    }

    public static void trace(Supplier<String> message) {
        if (DEBUG) {
            TRACE.add(message.get());
        }
    }

    public static List<String> traced() {
        return List.copyOf(TRACE);
    }
}
