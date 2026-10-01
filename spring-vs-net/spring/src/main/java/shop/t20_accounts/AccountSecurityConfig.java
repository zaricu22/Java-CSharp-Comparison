// VERDICT | T20 User accounts | BETTER: ASP.NET
// WHY: ASP.NET Identity + MapIdentityApi give a user store, registration, login tokens, password rules, hashing and lockout; Spring Security authenticates, but accounts, registration and password rules are hand-written.

package shop.t20_accounts;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;

/**
 * A second filter chain just for /api/account/**, authenticating against the accounts table.
 * No token endpoint exists in Spring Security itself, so login here is HTTP Basic.
 */
@Configuration
public class AccountSecurityConfig {

    @Bean
    @Order(1)
    SecurityFilterChain accountChain(HttpSecurity http, AccountService accounts) throws Exception {
        var provider = new DaoAuthenticationProvider(
                email -> accounts.load(email).orElseThrow(() -> new UsernameNotFoundException(email)));
        provider.setPasswordEncoder(accounts.encoder());

        return http
                .securityMatcher("/api/account/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/account/register").permitAll()
                        .anyRequest().authenticated())
                .authenticationManager(new ProviderManager(provider))
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
