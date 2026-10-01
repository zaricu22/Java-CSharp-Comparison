// VERDICT | T08 Events | BETTER: SPRING
// WHY: ApplicationEventPublisher + @EventListener / @TransactionalEventListener are built in; ASP.NET has no in-process event bus, so it is hand-written (or MediatR).

package shop.t08_events;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import shop.t05_repositories.ProductRepository;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Runs synchronously inside the order's transaction, so it sees the reduced stock. */
@Component
public class StockAlerts {

    private static final int LOW_STOCK = 5;

    private final ProductRepository products;
    private final List<String> alerts = new CopyOnWriteArrayList<>();

    public StockAlerts(ProductRepository products) {
        this.products = products;
    }

    @EventListener
    public void on(OrderPlaced event) {
        for (String sku : event.skus()) {
            products.findById(sku)
                    .filter(p -> p.getStock() < LOW_STOCK)
                    .ifPresent(p -> alerts.add(sku + " low: " + p.getStock()));
        }
    }

    public List<String> alerts() {
        return List.copyOf(alerts);
    }
}
