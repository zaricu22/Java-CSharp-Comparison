// VERDICT | T20 User accounts | BETTER: ASP.NET
// WHY: ASP.NET Identity + MapIdentityApi give a user store, registration, login tokens, password rules, hashing and lockout; Spring Security authenticates, but accounts, registration and password rules are hand-written.

package shop.t20_accounts;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Spring Security provides the building blocks (PasswordEncoder, UserDetails), but the account
 * store, registration, duplicate check and password policy are all hand-written here. Login tokens,
 * refresh, lockout, e-mail confirmation and 2FA would need even more code (or Spring Authorization Server).
 */
@Service
public class AccountService {

    public record RegisterRequest(@NotBlank @Email String email, @NotBlank String password) {}

    public static class RegistrationException extends RuntimeException {
        private final List<String> errors;

        public RegistrationException(List<String> errors) {
            super(String.join("; ", errors));
            this.errors = errors;
        }

        public List<String> errors() {
            return errors;
        }
    }

    private final AccountRepository accounts;
    private final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder(); // bcrypt

    public AccountService(AccountRepository accounts) {
        this.accounts = accounts;
    }

    /** The same rules as ASP.NET Identity's defaults - written out by hand. */
    static List<String> passwordProblems(String password) {
        List<String> problems = new ArrayList<>();
        if (password.length() < 6) problems.add("PasswordTooShort");
        if (password.chars().noneMatch(Character::isDigit)) problems.add("PasswordRequiresDigit");
        if (password.chars().noneMatch(Character::isLowerCase)) problems.add("PasswordRequiresLower");
        if (password.chars().noneMatch(Character::isUpperCase)) problems.add("PasswordRequiresUpper");
        if (password.chars().allMatch(Character::isLetterOrDigit)) problems.add("PasswordRequiresNonAlphanumeric");
        return problems;
    }

    @Transactional
    public void register(RegisterRequest request) {
        var problems = passwordProblems(request.password());
        if (accounts.existsById(request.email())) {
            problems.add(0, "DuplicateUserName");
        }
        if (!problems.isEmpty()) {
            throw new RegistrationException(problems);
        }
        accounts.save(new AccountEntity(request.email(), encoder.encode(request.password()), "USER"));
    }

    @Transactional(readOnly = true)
    public Optional<UserDetails> load(String email) {
        return accounts.findById(email).map(a -> User.withUsername(a.getEmail())
                .password(a.getPasswordHash()).roles(a.getRole()).build());
    }

    public PasswordEncoder encoder() {
        return encoder;
    }
}
