// VERDICT | T02 Dependency injection | BETTER: SPRING
// WHY: component scanning, Map<name, bean> injection and scoped proxies (request bean inside a singleton); ASP.NET registers everything by hand (easier to trace).

package shop.t02_di;

import org.springframework.stereotype.Component;

/**
 * A SINGLETON that depends on a REQUEST-scoped bean. Spring injects a scoped proxy, so every
 * call is routed to the current request's RequestInfo. ASP.NET refuses this combination
 * ("Cannot consume scoped service from singleton"), so there the logger must be scoped too.
 */
@Component
public class RequestLogger {

    private final RequestInfo requestInfo;

    public RequestLogger(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

    public String currentRequestId() {
        return requestInfo.id();
    }
}
