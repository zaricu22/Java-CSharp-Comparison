// VERDICT | T20 User accounts | BETTER: ASP.NET
// WHY: ASP.NET Identity + MapIdentityApi give a user store, registration, login tokens, password rules, hashing and lockout; Spring Security authenticates, but accounts, registration and password rules are hand-written.

package shop.t20_accounts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext // creates accounts
class AccountsTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    AccountRepository accounts;

    private ResultActions register(String email, String password) throws Exception {
        return mvc.perform(post("/api/account/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}"));
    }

    @Test
    void registerThenAuthenticate() throws Exception {
        register("eva@shop.rs", "Secret1!").andExpect(status().isOk());
        mvc.perform(get("/api/account/me").with(httpBasic("eva@shop.rs", "Secret1!")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("eva@shop.rs"));
    }

    @Test
    void weakPasswordListsEveryBrokenRule() throws Exception {
        register("weak@shop.rs", "abc").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", contains("PasswordTooShort", "PasswordRequiresDigit",
                        "PasswordRequiresUpper", "PasswordRequiresNonAlphanumeric")));
    }

    @Test
    void duplicateEmailIsRejected() throws Exception {
        register("dup@shop.rs", "Secret1!").andExpect(status().isOk());
        register("dup@shop.rs", "Secret1!").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasItem("DuplicateUserName")));
    }

    @Test
    void passwordIsStoredHashed() throws Exception {
        register("hash@shop.rs", "Secret1!").andExpect(status().isOk());
        assertThat(accounts.findById("hash@shop.rs").orElseThrow().getPasswordHash()).startsWith("{bcrypt}$2a$");
    }

    @Test
    void noLockoutAfterRepeatedFailures() throws Exception {
        register("lock@shop.rs", "Secret1!").andExpect(status().isOk());
        for (int i = 0; i < 5; i++) {
            mvc.perform(get("/api/account/me").with(httpBasic("lock@shop.rs", "wrong"))).andExpect(status().isUnauthorized());
        }
        // Nothing locks the account - lockout would have to be hand-written (Identity does it after 3 here)
        mvc.perform(get("/api/account/me").with(httpBasic("lock@shop.rs", "Secret1!"))).andExpect(status().isOk());
    }
}
