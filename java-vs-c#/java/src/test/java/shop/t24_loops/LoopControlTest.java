// VERDICT | T24 Labeled break & continue | BETTER: JAVA
// WHY: `break search;` / `continue orders;` leave or skip an outer loop directly; C# needs goto, a flag variable or an extra method.

package shop.t24_loops;

import org.junit.jupiter.api.Test;
import shop.domain.SampleData;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoopControlTest {

    @Test
    void breakOutOfBothLoops() {
        assertEquals("Blocked order 1: Keyboard", LoopControl.firstUnavailable(SampleData.orders(), Set.of("E1")));
        assertEquals("Blocked order 2: Coffee", LoopControl.firstUnavailable(SampleData.orders(), Set.of("G1")));
        assertEquals("all available", LoopControl.firstUnavailable(SampleData.orders(), Set.of()));
    }

    @Test
    void continueTheOuterLoop() {
        assertEquals(List.of(2, 3), LoopControl.shippableOrders(SampleData.orders(), Set.of("E1")));
        assertEquals(List.of(1, 2, 3, 4), LoopControl.shippableOrders(SampleData.orders(), Set.of()));
    }
}
