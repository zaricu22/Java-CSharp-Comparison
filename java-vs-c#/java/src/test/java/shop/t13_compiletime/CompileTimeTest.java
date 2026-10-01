// VERDICT | T13 Compile-time features | BETTER: C#
// WHY: partial classes, source generators, #if and [Conditional]; Java has no equivalents (subclass hooks, runtime flags).

package shop.t13_compiletime;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompileTimeTest {

    @Test
    void skuValidation() {
        assertTrue(SkuValidator.isValid("B1"));
        assertTrue(SkuValidator.isValid("E123"));
        assertFalse(SkuValidator.isValid("b1"));
        assertFalse(SkuValidator.isValid("E1234"));
    }

    @Test
    void generatedAndHandWrittenValidationCombine() {
        var form = new ProductForm();
        assertEquals(List.of("Name is required", "Price must be positive"), form.validate());
        form.setName("Coffee");
        form.setPrice(new BigDecimal("12.40"));
        assertTrue(form.validate().isEmpty());
    }

    @Test
    void buildModeIsDebug() {
        assertEquals("Debug", BuildInfo.mode());
    }

    @Test
    void traceRecordsInDebug() {
        BuildInfo.trace(() -> "from test");
        assertTrue(BuildInfo.traced().contains("from test"));
    }

    @Test
    void untypedSettingsNeedCasts() {
        var settings = DynamicSettings.defaults();
        assertEquals(40, DynamicSettings.nextPageSize(settings));
        settings.put("pageSize", "twenty"); // nothing stops a wrong type going in...
        assertThrows(ClassCastException.class, () -> DynamicSettings.nextPageSize(settings)); // ...until it is read
    }
}
