// VERDICT | Domain model | BETTER: ASP.NET
// WHY: EF Core maps plain classes by convention (properties, no annotations); JPA needs @Entity/@Id, a protected no-arg constructor and getters.

package shop.domain;

public enum Category { BOOKS, ELECTRONICS, GROCERY }
