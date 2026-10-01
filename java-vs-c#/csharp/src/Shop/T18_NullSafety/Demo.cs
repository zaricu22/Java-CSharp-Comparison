// VERDICT | T18 Null safety | BETTER: C#
// WHY: nullable reference types (checked by the compiler) plus ?. ?? ??=; Java has only Optional/null checks and NPEs at runtime.

namespace Shop.T18_NullSafety;

public static class Demo
{
    public static void Run()
    {
        var ana = new CustomerProfile("Ana", new Address("Belgrade", "Knez Mihailova 1"), null);
        var marko = new CustomerProfile("Marko", null, "+381 60 123");
        var ghost = new CustomerProfile("Ghost", null, null);

        Console.WriteLine($"Cities: {NullSafety.CityOf(ana)}, {NullSafety.CityOf(marko)}, {NullSafety.CityOf(null)}");
        Console.WriteLine($"Contacts: {NullSafety.Contact(ana)} | {NullSafety.Contact(marko)} | {NullSafety.Contact(ghost)}");
        Console.WriteLine($"Find Jelena: {NullSafety.Find([ana, marko], "Jelena")?.Name ?? "not found"}");
        Console.WriteLine("CityUnsafe does not exist: the compiler rejects c.Address.City when Address is nullable");
    }
}
