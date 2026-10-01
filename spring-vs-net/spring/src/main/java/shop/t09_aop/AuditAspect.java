// VERDICT | T09 AOP / cross-cutting | BETTER: SPRING
// WHY: an @Aspect intercepts any bean method, including the service layer; ASP.NET endpoint filters and middleware only wrap HTTP requests.

package shop.t09_aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Wraps every @Audited method of every bean - whether it is called from a controller,
 * a scheduled job, a message listener or another service. No caller has to opt in.
 */
@Aspect
@Component
public class AuditAspect {

    private final List<String> trail = new CopyOnWriteArrayList<>();

    @Around("@annotation(shop.t09_aop.Audited)")
    public Object audit(ProceedingJoinPoint call) throws Throwable {
        String name = call.getSignature().getDeclaringType().getSimpleName() + "." + call.getSignature().getName();
        try {
            Object result = call.proceed();
            trail.add(name + " ok");
            return result;
        } catch (Throwable failure) {
            trail.add(name + " failed: " + failure.getClass().getSimpleName());
            throw failure;
        }
    }

    public List<String> trail() {
        return List.copyOf(trail);
    }
}
