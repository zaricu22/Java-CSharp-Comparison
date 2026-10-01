// VERDICT | T01 Properties & records | BETTER: C#
// WHY: properties (`field`, `required`, `init`) and `with` replace Java's hand-written getters/setters and manual record copies.

package shop.t01_properties;

/**
 * A mutable entity with: a read-only id, a validated field, plain fields,
 * a field that only the class may change and one computed value.
 * <p>
 * Java has no properties, so every one of those is a hand-written method (or Lombok).
 */
public class CustomerAccount {

    private final int id;
    private String email;
    private String firstName;
    private String lastName;
    private int loyaltyPoints;

    public CustomerAccount(int id, String email, String firstName, String lastName) {
        this.id = id;
        setEmail(email);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email: " + email);
        }
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void addPoints(int points) {
        loyaltyPoints += points;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
