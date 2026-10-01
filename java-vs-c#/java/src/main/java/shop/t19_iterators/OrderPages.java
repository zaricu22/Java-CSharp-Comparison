// VERDICT | T19 Iterators & generators | BETTER: C#
// WHY: yield return and async streams (await foreach) write the state machine for you; Java needs a hand-written Iterator.

package shop.t19_iterators;

import shop.domain.Order;

import java.util.List;

/** Fake paged API ("GET /orders?page=N&size=M") that counts how many pages were requested. */
public final class OrderPages {

    private final List<Order> source;
    private int pagesFetched;

    public OrderPages(List<Order> source) {
        this.source = source;
    }

    public List<Order> page(int index, int size) {
        pagesFetched++;
        int from = index * size;
        if (from >= source.size()) {
            return List.of();
        }
        return source.subList(from, Math.min(from + size, source.size()));
    }

    public int pagesFetched() {
        return pagesFetched;
    }
}
