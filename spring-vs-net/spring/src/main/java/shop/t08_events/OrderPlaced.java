// VERDICT | T08 Events | BETTER: SPRING
// WHY: ApplicationEventPublisher + @EventListener / @TransactionalEventListener are built in; ASP.NET has no in-process event bus, so it is hand-written (or MediatR).

package shop.t08_events;

import java.util.List;

/** Any object can be an event - no base class or marker interface needed. */
public record OrderPlaced(long orderId, String customer, List<String> skus) {}
