// VERDICT | T20 User accounts | BETTER: ASP.NET
// WHY: ASP.NET Identity + MapIdentityApi give a user store, registration, login tokens, password rules, hashing and lockout; Spring Security authenticates, but accounts, registration and password rules are hand-written.

package shop.t20_accounts;

import org.springframework.data.jpa.repository.JpaRepository;

/** Top-level on purpose: Spring Data ignores repository interfaces nested in other classes by default. */
public interface AccountRepository extends JpaRepository<AccountEntity, String> {
}
