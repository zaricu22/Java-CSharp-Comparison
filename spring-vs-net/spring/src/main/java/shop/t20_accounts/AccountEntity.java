// VERDICT | T20 User accounts | BETTER: ASP.NET
// WHY: ASP.NET Identity + MapIdentityApi give a user store, registration, login tokens, password rules, hashing and lockout; Spring Security authenticates, but accounts, registration and password rules are hand-written.

package shop.t20_accounts;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Hand-written user table (ASP.NET Identity brings AspNetUsers, roles, claims, logins, tokens...). */
@Entity
@Table(name = "accounts")
public class AccountEntity {

    @Id
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String role;

    protected AccountEntity() {
        // required by JPA
    }

    public AccountEntity(String email, String passwordHash, String role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRole() {
        return role;
    }
}
