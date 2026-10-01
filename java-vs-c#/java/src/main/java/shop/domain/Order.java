// VERDICT | Domain model | BETTER: C#
// WHY: positional records with computed members fit in one file; Java needs one file per public type and BigDecimal methods instead of decimal operators.

package shop.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record Order(int id, Customer customer, LocalDate date, List<OrderLine> lines) {

    public Order {
        lines = List.copyOf(lines); // defensive, immutable copy
    }

    public BigDecimal total() {
        return lines.stream()
                .map(OrderLine::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
