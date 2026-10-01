// VERDICT | T18 Null safety | BETTER: C#
// WHY: nullable reference types (checked by the compiler) plus ?. ?? ??=; Java has only Optional/null checks and NPEs at runtime.

package shop.t18_nullsafety;

import org.junit.jupiter.api.Test;
import shop.t18_nullsafety.CustomerProfile.Address;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NullSafetyTest {

    private final CustomerProfile ana = new CustomerProfile("Ana", new Address("Belgrade", "Knez Mihailova 1"), null);
    private final CustomerProfile marko = new CustomerProfile("Marko", null, "+381 60 123");
    private final CustomerProfile ghost = new CustomerProfile("Ghost", null, null);

    @Test
    void safeNavigationWithDefault() {
        assertEquals("Belgrade", NullSafety.cityOf(ana));
        assertEquals("unknown", NullSafety.cityOf(marko));
        assertEquals("unknown", NullSafety.cityOf(null));
    }

    @Test
    void optionalLengthAndFallbackChain() {
        assertEquals(0, NullSafety.phoneLength(ana));
        assertEquals(11, NullSafety.phoneLength(marko));
        assertEquals("Knez Mihailova 1", NullSafety.contact(ana));
        assertEquals("+381 60 123", NullSafety.contact(marko));
        assertEquals("no contact", NullSafety.contact(ghost));
    }

    @Test
    void unsafeDereferenceCompilesAndFailsAtRuntime() {
        assertThrows(NullPointerException.class, () -> NullSafety.cityUnsafe(marko));
    }

    @Test
    void findReturnsOptional() {
        assertEquals(Optional.of(ana), NullSafety.find(List.of(ana, marko), "Ana"));
        assertEquals(Optional.empty(), NullSafety.find(List.of(ana, marko), "Jelena"));
    }

    @Test
    void lazyInitialisationHappensOnce() {
        assertSame(NullSafety.auditLog(), NullSafety.auditLog());
    }
}
