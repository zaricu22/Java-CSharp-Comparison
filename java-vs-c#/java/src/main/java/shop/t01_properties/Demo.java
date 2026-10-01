// VERDICT | T01 Properties & records | BETTER: C#
// WHY: properties (`field`, `required`, `init`) and `with` replace Java's hand-written getters/setters and manual record copies.

package shop.t01_properties;

import shop.domain.SampleData;

public final class Demo {

    public static void run() {
        var ana = new CustomerAccount(1, "ana@shop.rs", "Ana", "Jovanovic");
        ana.setLastName("Petrovic");
        ana.addPoints(150);
        System.out.println(ana.getFullName() + " <" + ana.getEmail() + "> points=" + ana.getLoyaltyPoints());

        System.out.println("Discounted: " + Pricing.discounted(SampleData.CLEAN_CODE, 10));
    }
}
