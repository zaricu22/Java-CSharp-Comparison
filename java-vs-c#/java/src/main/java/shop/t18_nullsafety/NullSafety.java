// VERDICT | T18 Null safety | BETTER: C#
// WHY: nullable reference types (checked by the compiler) plus ?. ?? ??=; Java has only Optional/null checks and NPEs at runtime.

package shop.t18_nullsafety;

import shop.t18_nullsafety.CustomerProfile.Address;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * No null-safety in the type system and no ?. / ?? / ??= operators.
 * The tools are Optional chains (allocations + verbosity) or explicit null checks,
 * and the compiler never warns when a possibly-null value is dereferenced.
 */
public final class NullSafety {

    private static List<String> auditLog;

    private NullSafety() {}

    public static String cityOf(CustomerProfile customer) {
        return Optional.ofNullable(customer)
                .map(CustomerProfile::address)
                .map(Address::city)
                .orElse("unknown");
    }

    public static int phoneLength(CustomerProfile customer) {
        return customer.phone() == null ? 0 : customer.phone().length();
    }

    /** First available contact: phone, then street, then a default. */
    public static String contact(CustomerProfile customer) {
        return Optional.ofNullable(customer.phone())
                .or(() -> Optional.ofNullable(customer.address()).map(Address::street))
                .orElse("no contact");
    }

    /** Compiles without any warning - and throws NullPointerException when address is null. */
    public static String cityUnsafe(CustomerProfile customer) {
        return customer.address().city();
    }

    public static Optional<CustomerProfile> find(List<CustomerProfile> all, String name) {
        return all.stream().filter(c -> c.name().equals(name)).findFirst();
    }

    /** Lazy initialisation: "if null then assign". */
    public static List<String> auditLog() {
        if (auditLog == null) {
            auditLog = new ArrayList<>();
        }
        return auditLog;
    }
}
