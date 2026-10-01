// VERDICT | T02 Dependency injection | BETTER: SPRING
// WHY: component scanning, Map<name, bean> injection and scoped proxies (request bean inside a singleton); ASP.NET registers everything by hand (easier to trace).

package shop.t02_di;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

/** One instance per HTTP request (ASP.NET: AddScoped). */
@Component
@RequestScope
public class RequestInfo {

    private final String id = UUID.randomUUID().toString();

    public String id() {
        return id;
    }
}
