// VERDICT | T20 User accounts | BETTER: ASP.NET
// WHY: ASP.NET Identity + MapIdentityApi give a user store, registration, login tokens, password rules, hashing and lockout; Spring Security authenticates, but accounts, registration and password rules are hand-written.

package shop.t20_accounts;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import shop.t20_accounts.AccountService.RegisterRequest;
import shop.t20_accounts.AccountService.RegistrationException;

import java.util.Map;

@RestController
public class AccountController {

    private final AccountService accounts;

    public AccountController(AccountService accounts) {
        this.accounts = accounts;
    }

    @PostMapping("/api/account/register")
    public void register(@Valid @RequestBody RegisterRequest request) {
        accounts.register(request);
    }

    @GetMapping("/api/account/me")
    public Map<String, String> me(Authentication authentication) {
        return Map.of("email", authentication.getName());
    }

    @ExceptionHandler(RegistrationException.class)
    public ProblemDetail registrationFailed(RegistrationException e) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Registration failed");
        problem.setProperty("errors", e.errors());
        return problem;
    }
}
