// VERDICT | T07 Transactions | BETTER: SPRING
// WHY: @Transactional makes a whole service method atomic across repositories and nested calls; EF's SaveChanges is one unit of work, several saves need BeginTransaction/Commit.

package shop.t07_transactions;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.domain.OrderEntity;
import shop.domain.OrderLineEntity;
import shop.t05_repositories.ProductRepository;
import shop.t06_queries.OrderRepository;
import shop.t08_events.OrderPlaced;
import shop.t09_aop.Audited;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class OrderService {

    public record OrderConfirmation(long orderId, BigDecimal total) {}

    public static class UnknownProductException extends RuntimeException {
        public UnknownProductException(String sku) {
            super("Unknown product " + sku);
        }
    }

    private final ProductRepository products;
    private final OrderRepository orders;
    private final ApplicationEventPublisher events;

    public OrderService(ProductRepository products, OrderRepository orders, ApplicationEventPublisher events) {
        this.products = products;
        this.orders = orders;
        this.events = events;
    }

    /**
     * One annotation: stock changes on every product, the new order and anything the event
     * listeners write either ALL commit or ALL roll back. If the second line is out of stock,
     * the stock already taken for the first line is restored automatically.
     */
    @Audited
    @Transactional
    public OrderConfirmation placeOrder(OrderRequest request) {
        var order = new OrderEntity(request.customer(), LocalDate.now());
        for (var line : request.lines()) {
            var product = products.findById(line.sku()).orElseThrow(() -> new UnknownProductException(line.sku()));
            product.removeStock(line.quantity()); // may throw InsufficientStockException -> rollback
            order.addLine(product, line.quantity());
        }
        orders.save(order);
        events.publishEvent(new OrderPlaced(order.getId(), order.getCustomer(),
                order.getLines().stream().map(OrderLineEntity::getProduct).map(p -> p.getSku()).toList()));
        return new OrderConfirmation(order.getId(), order.total());
    }
}
