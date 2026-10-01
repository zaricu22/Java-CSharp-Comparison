// VERDICT | T01 Endpoints & hosting | BETTER: ASP.NET
// WHY: minimal APIs map a route to a lambda with typed results in one line; Spring needs a @RestController class (Java 21 sealed switch maps results as cleanly as C#).

package shop.t01_endpoints;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.domain.Category;
import shop.t01_endpoints.RegisterResult.Created;
import shop.t01_endpoints.RegisterResult.DuplicateSku;
import shop.t04_validation.NewProduct;

import java.net.URI;
import java.util.List;

/**
 * A class per resource, one annotated method per route. Handlers are plain blocking methods:
 * with spring.threads.virtual.enabled=true each request runs on a virtual thread, so
 * there is no need for async signatures (the C# side uses async/await instead).
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final CatalogService catalog;

    public ProductController(CatalogService catalog) {
        this.catalog = catalog;
    }

    @GetMapping
    public List<ProductDto> list(@RequestParam(required = false) Category category) {
        return catalog.list(category);
    }

    @GetMapping("/{sku}")
    public ProductDto get(@PathVariable String sku) {
        return catalog.find(sku).orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND,
                ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Product " + sku + " not found"), null));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody NewProduct body) {
        // Java 21: exhaustive switch over a sealed interface - no default branch needed
        return switch (catalog.register(body)) {
            case Created(var product) -> ResponseEntity.created(URI.create("/api/products/" + product.sku())).body(product);
            case DuplicateSku(var sku) -> ResponseEntity.of(
                    ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "SKU " + sku + " already exists")).build();
        };
    }
}
