// VERDICT | T22 Resource cleanup | BETTER: TIE
// WHY: C# `using var` needs no nesting; Java try-with-resources keeps the original exception and records close() failures as suppressed.

package shop.t22_resources;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResourcesTest {

    @Test
    void resourcesCloseInReverseOrder() {
        List<String> journal = new ArrayList<>();
        Exporter.exportOrders(journal, "1,Ana", false);
        assertEquals(List.of("orders.csv: opened", "lines.csv: opened", "orders.csv: 1,Ana",
                "lines.csv: 1,B1,2", "lines.csv: closed", "orders.csv: closed"), journal);
    }

    @Test
    void resourcesCloseWhenBodyFails() {
        List<String> journal = new ArrayList<>();
        assertThrows(IllegalStateException.class, () -> Exporter.exportOrders(journal, "", false));
        assertEquals(List.of("orders.csv: opened", "lines.csv: opened", "lines.csv: closed", "orders.csv: closed"), journal);
    }

    @Test
    void originalExceptionIsKeptAndCloseFailureIsSuppressed() {
        var ex = assertThrows(IllegalStateException.class, () -> Exporter.exportOrders(new ArrayList<>(), "", true));
        assertEquals("write failed", ex.getMessage());
        assertEquals("close failed", ex.getSuppressed()[0].getMessage());
    }
}
