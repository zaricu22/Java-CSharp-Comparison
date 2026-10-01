// VERDICT | T18 Null safety | BETTER: C#
// WHY: nullable reference types (checked by the compiler) plus ?. ?? ??=; Java has only Optional/null checks and NPEs at runtime.

package shop.t18_nullsafety;

/**
 * Which fields may be null is only a comment: the type system cannot express it.
 * address and phone are optional, street inside Address is optional.
 */
public record CustomerProfile(String name, Address address, String phone) {

    public record Address(String city, String street) {}
}
