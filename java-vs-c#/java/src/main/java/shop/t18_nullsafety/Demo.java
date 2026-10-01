// VERDICT | T18 Null safety | BETTER: C#
// WHY: nullable reference types (checked by the compiler) plus ?. ?? ??=; Java has only Optional/null checks and NPEs at runtime.

package shop.t18_nullsafety;

import shop.t18_nullsafety.CustomerProfile.Address;

import java.util.List;

public final class Demo {

    public static void run() {
        var ana = new CustomerProfile("Ana", new Address("Belgrade", "Knez Mihailova 1"), null);
        var marko = new CustomerProfile("Marko", null, "+381 60 123");
        var ghost = new CustomerProfile("Ghost", null, null);

        System.out.println("Cities: " + NullSafety.cityOf(ana) + ", " + NullSafety.cityOf(marko) + ", " + NullSafety.cityOf(null));
        System.out.println("Contacts: " + NullSafety.contact(ana) + " | " + NullSafety.contact(marko) + " | " + NullSafety.contact(ghost));
        System.out.println("Find Jelena: " + NullSafety.find(List.of(ana, marko), "Jelena").map(CustomerProfile::name).orElse("not found"));
        try {
            NullSafety.cityUnsafe(marko);
        } catch (NullPointerException _) {
            System.out.println("cityUnsafe(marko) compiled fine and threw NPE at runtime");
        }
    }
}
