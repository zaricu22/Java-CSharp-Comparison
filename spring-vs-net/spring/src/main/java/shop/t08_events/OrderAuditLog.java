// VERDICT | T08 Events | BETTER: SPRING
// WHY: ApplicationEventPublisher + @EventListener / @TransactionalEventListener are built in; ASP.NET has no in-process event bus, so it is hand-written (or MediatR).

package shop.t08_events;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @TransactionalEventListener runs only AFTER the surrounding transaction commits:
 * a rolled-back order never reaches the audit log (ideal for e-mails, messages, webhooks).
 */
@Component
public class OrderAuditLog {

    private final List<String> entries = new CopyOnWriteArrayList<>();

    @TransactionalEventListener
    public void on(OrderPlaced event) {
        entries.add("order " + event.orderId() + " by " + event.customer());
    }

    public List<String> entries() {
        return List.copyOf(entries);
    }
}
