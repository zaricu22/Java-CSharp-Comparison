// VERDICT | Domain model | BETTER: ASP.NET
// WHY: EF Core maps plain classes by convention (properties, no annotations); JPA needs @Entity/@Id, a protected no-arg constructor and getters.

package shop.domain;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.t05_repositories.ProductRepository;
import shop.t06_queries.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Same seed data as the .NET side (and as the java-vs-c# project), so both test suites assert identical numbers. */
@Component
public class DataSeeder implements ApplicationRunner {

    private final ProductRepository products;
    private final OrderRepository orders;

    public DataSeeder(ProductRepository products, OrderRepository orders) {
        this.products = products;
        this.orders = orders;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (products.count() > 0) {
            return;
        }
        var cleanCode = new ProductEntity("B1", "Clean Code", Category.BOOKS, new BigDecimal("35.50"), 12);
        var refactoring = new ProductEntity("B2", "Refactoring", Category.BOOKS, new BigDecimal("42.00"), 7);
        var keyboard = new ProductEntity("E1", "Keyboard", Category.ELECTRONICS, new BigDecimal("89.99"), 3);
        var monitor = new ProductEntity("E2", "Monitor", Category.ELECTRONICS, new BigDecimal("249.00"), 0);
        var coffee = new ProductEntity("G1", "Coffee", Category.GROCERY, new BigDecimal("12.40"), 40);
        products.saveAll(List.of(cleanCode, refactoring, keyboard, monitor, coffee));

        orders.save(order("Ana", LocalDate.of(2026, 1, 10), new Line(cleanCode, 2), new Line(keyboard, 1)));
        orders.save(order("Marko", LocalDate.of(2026, 1, 15), new Line(monitor, 1), new Line(coffee, 3)));
        orders.save(order("Ana", LocalDate.of(2026, 2, 2), new Line(coffee, 5)));
        orders.save(order("Jelena", LocalDate.of(2026, 2, 20), new Line(refactoring, 1), new Line(keyboard, 2)));
    }

    private record Line(ProductEntity product, int quantity) {}

    private static OrderEntity order(String customer, LocalDate date, Line... lines) {
        var order = new OrderEntity(customer, date);
        for (Line line : lines) {
            order.addLine(line.product(), line.quantity());
        }
        return order;
    }
}
