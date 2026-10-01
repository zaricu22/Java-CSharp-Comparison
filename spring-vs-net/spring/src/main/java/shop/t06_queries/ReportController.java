// VERDICT | T06 Complex queries | BETTER: ASP.NET
// WHY: LINQ is type-checked and refactor-safe (GroupBy/Sum/conditional Where); Spring uses JPQL strings or the verbose Criteria/Specification API with string attribute names.

package shop.t06_queries;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.domain.Category;
import shop.domain.OrderEntity;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final OrderRepository orders;

    public ReportController(OrderRepository orders) {
        this.orders = orders;
    }

    @GetMapping("/revenue-by-category")
    public List<CategoryRevenue> revenueByCategory() {
        return orders.revenueByCategory();
    }

    @GetMapping("/top-customers")
    public List<CustomerTotal> topCustomers(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate since,
                                            @RequestParam(defaultValue = "3") int limit) {
        return orders.topCustomersSince(since, PageRequest.of(0, limit));
    }

    /** Order ids, optionally filtered by customer and/or a category contained in the order. */
    @GetMapping("/orders")
    @Transactional(readOnly = true)
    public List<Long> orders(@RequestParam(required = false) String customer,
                             @RequestParam(required = false) Category category) {
        return orders.findAll(OrderSpecifications.matching(customer, category), Sort.by("id")).stream()
                .map(OrderEntity::getId).toList();
    }
}
