// VERDICT | T19 Iterators & generators | BETTER: C#
// WHY: yield return and async streams (await foreach) write the state machine for you; Java needs a hand-written Iterator.

package shop.t19_iterators;

import org.junit.jupiter.api.Test;
import shop.domain.Order;
import shop.domain.SampleData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IteratorsTest {

    @Test
    void infiniteSequenceSkipsWeekends() {
        assertEquals(List.of(LocalDate.of(2026, 1, 12), LocalDate.of(2026, 1, 13), LocalDate.of(2026, 1, 14)),
                Iterators.deliveryDays(LocalDate.of(2026, 1, 9)).limit(3).toList());
    }

    @Test
    void pagedIteratorReadsUntilEmptyPage() {
        var api = new OrderPages(SampleData.orders());
        assertEquals(List.of(1, 2, 3, 4), Iterators.allOrdersStream(api, 3).map(Order::id).toList());
        assertEquals(3, api.pagesFetched()); // [1,2,3], [4], []
    }

    @Test
    void pagedIteratorIsLazy() {
        var api = new OrderPages(SampleData.orders());
        assertEquals(List.of(1, 2), Iterators.allOrdersStream(api, 3).limit(2).map(Order::id).toList());
        assertEquals(1, api.pagesFetched());
    }

    @Test
    void handWrittenIteratorFollowsTheProtocol() {
        var iterator = Iterators.allOrders(new OrderPages(List.of()), 3);
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);

        List<Integer> ids = new ArrayList<>();
        Iterators.allOrders(new OrderPages(SampleData.orders()), 2).forEachRemaining(o -> ids.add(o.id()));
        assertEquals(List.of(1, 2, 3, 4), ids);
    }
}
