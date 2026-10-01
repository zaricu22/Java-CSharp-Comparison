// VERDICT | T08 Events | BETTER: SPRING
// WHY: ApplicationEventPublisher + @EventListener / @TransactionalEventListener are built in; ASP.NET has no in-process event bus, so it is hand-written (or MediatR).

package shop.t08_events;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.support.TransactionTemplate;
import shop.t07_transactions.OrderRequest;
import shop.t07_transactions.OrderService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext // commits orders
class EventsTest {

    @Autowired
    OrderService orders;

    @Autowired
    StockAlerts stockAlerts;

    @Autowired
    OrderAuditLog auditLog;

    @Autowired
    TransactionTemplate transactions;

    @Test
    void listenersReactToPlacedOrder() {
        var confirmation = orders.placeOrder(new OrderRequest("Eva", List.of(new OrderRequest.Line("E1", 1))));

        assertThat(stockAlerts.alerts()).contains("E1 low: 2");
        assertThat(auditLog.entries()).contains("order " + confirmation.orderId() + " by Eva");
    }

    @Test
    void transactionalListenerSkipsRolledBackOrders() {
        transactions.executeWithoutResult(status -> {
            orders.placeOrder(new OrderRequest("Rollback", List.of(new OrderRequest.Line("G1", 1))));
            status.setRollbackOnly();
        });

        assertThat(auditLog.entries()).noneMatch(entry -> entry.endsWith("by Rollback"));
    }
}
