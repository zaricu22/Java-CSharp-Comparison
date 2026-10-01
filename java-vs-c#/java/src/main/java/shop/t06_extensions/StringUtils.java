// VERDICT | T06 Extension methods | BETTER: C#
// WHY: extension methods/properties read left-to-right on the object; Java needs static Utils classes called inside-out.

package shop.t06_extensions;

import java.util.Locale;

/** Java cannot add methods to String, so behaviour goes into a static "Utils" class. */
public final class StringUtils {

    private StringUtils() {}

    public static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }

    public static String toSlug(String s) {
        return s.toLowerCase(Locale.ROOT).trim()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
