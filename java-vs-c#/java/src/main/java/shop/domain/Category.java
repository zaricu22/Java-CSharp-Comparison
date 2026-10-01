// VERDICT | Domain model | BETTER: C#
// WHY: positional records with computed members fit in one file; Java needs one file per public type and BigDecimal methods instead of decimal operators.

package shop.domain;

public enum Category { BOOKS, ELECTRONICS, GROCERY }
