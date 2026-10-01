// VERDICT | Domain model | BETTER: C#
// WHY: positional records with computed members fit in one file; Java needs one file per public type and BigDecimal methods instead of decimal operators.

package shop.domain;

import java.math.BigDecimal;

public record Product(String sku, String name, Category category, BigDecimal price) {}
