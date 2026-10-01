// VERDICT | T01 Endpoints & hosting | BETTER: ASP.NET
// WHY: minimal APIs map a route to a lambda with typed results in one line; Spring needs a @RestController class (Java 21 sealed switch maps results as cleanly as C#).

package shop.t01_endpoints;

/** Outcome of registering a product; the controller turns each case into an HTTP response. */
public sealed interface RegisterResult {

    record Created(ProductDto product) implements RegisterResult {}

    record DuplicateSku(String sku) implements RegisterResult {}
}
