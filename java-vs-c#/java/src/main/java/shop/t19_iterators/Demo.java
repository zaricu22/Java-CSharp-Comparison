// VERDICT | T19 Iterators & generators | BETTER: C#
// WHY: yield return and async streams (await foreach) write the state machine for you; Java needs a hand-written Iterator.

package shop.t19_iterators;

import shop.domain.Order;
import shop.domain.SampleData;

import java.time.LocalDate;

public final class Demo {

    public static void run() {
        System.out.println("Next 3 delivery days after Fri 2026-01-09: "
                + Iterators.deliveryDays(LocalDate.of(2026, 1, 9)).limit(3).toList());

        var api = new OrderPages(SampleData.orders());
        var ids = Iterators.allOrdersStream(api, 3).map(Order::id).toList();
        System.out.println("All order ids: " + ids + " (pages fetched: " + api.pagesFetched() + ")");

        var lazyApi = new OrderPages(SampleData.orders());
        var firstTwo = Iterators.allOrdersStream(lazyApi, 3).limit(2).map(Order::id).toList();
        System.out.println("First two: " + firstTwo + " (pages fetched: " + lazyApi.pagesFetched() + ")");
    }
}
