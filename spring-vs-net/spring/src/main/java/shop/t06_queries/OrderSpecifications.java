// VERDICT | T06 Complex queries | BETTER: ASP.NET
// WHY: LINQ is type-checked and refactor-safe (GroupBy/Sum/conditional Where); Spring uses JPQL strings or the verbose Criteria/Specification API with string attribute names.

package shop.t06_queries;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import shop.domain.Category;
import shop.domain.OrderEntity;
import shop.domain.OrderLineEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Dynamic filters (each one optional) need the Criteria API. Attribute names are strings
 * ("customer", "lines", "product") unless the JPA metamodel generator is added to the build.
 */
public final class OrderSpecifications {

    private OrderSpecifications() {}

    public static Specification<OrderEntity> matching(String customer, Category category) {
        List<Specification<OrderEntity>> filters = new ArrayList<>();
        if (customer != null) {
            filters.add((root, query, cb) -> cb.equal(root.get("customer"), customer));
        }
        if (category != null) {
            filters.add((root, query, cb) -> {
                query.distinct(true);
                Join<OrderEntity, OrderLineEntity> lines = root.join("lines", JoinType.INNER);
                return cb.equal(lines.get("product").get("category"), category);
            });
        }
        return Specification.allOf(filters);
    }
}
