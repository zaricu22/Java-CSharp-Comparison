// VERDICT | T19 Iterators & generators | BETTER: C#
// WHY: yield return and async streams (await foreach) write the state machine for you; Java needs a hand-written Iterator.

package shop.t19_iterators;

import shop.domain.Order;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * No yield: a lazy sequence with its own control flow must be written as an Iterator
 * state machine (fields + hasNext/next). Simple infinite sequences are fine with
 * Stream.iterate, and there is no built-in async stream (await foreach).
 */
public final class Iterators {

    private Iterators() {}

    /** Easy case - Stream.iterate + filter is as good as yield here. */
    public static Stream<LocalDate> deliveryDays(LocalDate from) {
        return Stream.iterate(from.plusDays(1), d -> d.plusDays(1))
                .filter(d -> d.getDayOfWeek() != DayOfWeek.SATURDAY && d.getDayOfWeek() != DayOfWeek.SUNDAY);
    }

    /** Hard case - "fetch pages until an empty one" needs a hand-written state machine. */
    public static Iterator<Order> allOrders(OrderPages api, int pageSize) {
        return new Iterator<>() {
            private int nextPage;
            private Iterator<Order> current = Collections.emptyIterator();
            private boolean exhausted;

            @Override
            public boolean hasNext() {
                while (!current.hasNext() && !exhausted) {
                    List<Order> items = api.page(nextPage++, pageSize);
                    if (items.isEmpty()) {
                        exhausted = true;
                    } else {
                        current = items.iterator();
                    }
                }
                return current.hasNext();
            }

            @Override
            public Order next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return current.next();
            }
        };
    }

    /** ...and turning an Iterator into a Stream takes one more adapter. */
    public static Stream<Order> allOrdersStream(OrderPages api, int pageSize) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(allOrders(api, pageSize), Spliterator.ORDERED), false);
    }
}
