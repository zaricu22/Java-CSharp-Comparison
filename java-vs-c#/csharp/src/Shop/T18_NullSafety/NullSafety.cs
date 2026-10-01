// VERDICT | T18 Null safety | BETTER: C#
// WHY: nullable reference types (checked by the compiler) plus ?. ?? ??=; Java has only Optional/null checks and NPEs at runtime.

namespace Shop.T18_NullSafety;

// "?" in the type says what may be null; the compiler tracks it (Nullable enabled and
// treated as errors in Directory.Build.props), so dereferencing Address without a check fails the build.
public sealed record Address(string City, string? Street);

public sealed record CustomerProfile(string Name, Address? Address, string? Phone);

public static class NullSafety
{
    private static List<string>? _auditLog;

    public static string CityOf(CustomerProfile? customer) => customer?.Address?.City ?? "unknown";

    public static int PhoneLength(CustomerProfile customer) => customer.Phone?.Length ?? 0;

    /// <summary>First available contact: phone, then street, then a default.</summary>
    public static string Contact(CustomerProfile customer) => customer.Phone ?? customer.Address?.Street ?? "no contact";

    // public static string CityUnsafe(CustomerProfile c) => c.Address.City;
    //   -> error CS8602: Dereference of a possibly null reference. The Java version compiles.

    public static CustomerProfile? Find(IEnumerable<CustomerProfile> all, string name) =>
        all.FirstOrDefault(c => c.Name == name);

    /// <summary>Lazy initialisation with ??=.</summary>
    public static List<string> AuditLog => _auditLog ??= [];
}
