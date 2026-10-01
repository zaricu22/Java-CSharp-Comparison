// VERDICT | T06 Complex queries | BETTER: ASP.NET
// WHY: LINQ is type-checked and refactor-safe (GroupBy/Sum/conditional Where); Spring uses JPQL strings or the verbose Criteria/Specification API with string attribute names.

package shop.t06_queries;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.domain.OrderEntity;

import java.time.LocalDate;
import java.util.List;

/**
 * Aggregations do not fit method-name derivation, so they are JPQL strings. Hibernate checks
 * them at startup, but the compiler does not: renaming a field breaks them silently until boot.
 */
public interface OrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {

    @Query("""
            select new shop.t06_queries.CategoryRevenue(p.category, sum(l.unitPrice * l.quantity))
            from OrderLineEntity l join l.product p
            group by p.category
            order by sum(l.unitPrice * l.quantity) desc""")
    List<CategoryRevenue> revenueByCategory();

    @Query("""
            select new shop.t06_queries.CustomerTotal(o.customer, sum(l.unitPrice * l.quantity))
            from OrderEntity o join o.lines l
            where o.placedOn >= :from
            group by o.customer
            order by sum(l.unitPrice * l.quantity) desc""")
    List<CustomerTotal> topCustomersSince(@Param("from") LocalDate from, Pageable page);
}
