// VERDICT | T09 AOP / cross-cutting | BETTER: SPRING
// WHY: an @Aspect intercepts any bean method, including the service layer; ASP.NET endpoint filters and middleware only wrap HTTP requests.

package shop.t09_aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marks a service method whose calls and failures go to the audit trail. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audited {
}
