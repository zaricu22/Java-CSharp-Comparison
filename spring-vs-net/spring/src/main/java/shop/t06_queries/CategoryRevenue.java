// VERDICT | T06 Complex queries | BETTER: ASP.NET
// WHY: LINQ is type-checked and refactor-safe (GroupBy/Sum/conditional Where); Spring uses JPQL strings or the verbose Criteria/Specification API with string attribute names.

package shop.t06_queries;

import shop.domain.Category;

import java.math.BigDecimal;

public record CategoryRevenue(Category category, BigDecimal revenue) {}
