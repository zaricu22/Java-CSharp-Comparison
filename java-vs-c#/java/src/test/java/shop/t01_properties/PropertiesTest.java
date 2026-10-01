// VERDICT | T01 Properties & records | BETTER: C#
// WHY: properties (`field`, `required`, `init`) and `with` replace Java's hand-written getters/setters and manual record copies.

package shop.t01_properties;

import org.junit.jupiter.api.Test;
import shop.domain.SampleData;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PropertiesTest {

    private CustomerAccount ana() {
        return new CustomerAccount(1, "ana@shop.rs", "Ana", "Jovanovic");
    }

    @Test
    void fullNameIsComputedFromParts() {
        var ana = ana();
        ana.setLastName("Petrovic");
        assertEquals("Ana Petrovic", ana.getFullName());
    }

    @Test
    void invalidEmailIsRejectedOnCreateAndOnChange() {
        assertThrows(IllegalArgumentException.class, () -> new CustomerAccount(2, "nope", "A", "B"));
        var ana = ana();
        assertThrows(IllegalArgumentException.class, () -> ana.setEmail("still-nope"));
        assertEquals("ana@shop.rs", ana.getEmail());
    }

    @Test
    void loyaltyPointsChangeOnlyThroughMethod() {
        var ana = ana();
        ana.addPoints(100);
        ana.addPoints(50);
        assertEquals(150, ana.getLoyaltyPoints());
    }

    @Test
    void recordCopyChangesOnlyThePrice() {
        var discounted = Pricing.discounted(SampleData.CLEAN_CODE, 10);
        assertEquals(new BigDecimal("31.95"), discounted.price());
        assertEquals(SampleData.CLEAN_CODE.name(), discounted.name());
        assertEquals(new BigDecimal("35.50"), SampleData.CLEAN_CODE.price()); // original untouched
    }
}
