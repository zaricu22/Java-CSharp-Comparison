// VERDICT | T01 Endpoints & hosting | BETTER: ASP.NET
// WHY: minimal APIs map a route to a lambda with typed results in one line; Spring needs a @RestController class (Java 21 sealed switch maps results as cleanly as C#).

package shop.t01_endpoints;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.domain.Category;
import shop.domain.ProductEntity;
import shop.t04_validation.NewProduct;
import shop.t05_repositories.ProductRepository;
import shop.t09_aop.Audited;

import java.util.List;
import java.util.Optional;

@Service
public class CatalogService {

    private final ProductRepository products;

    public CatalogService(ProductRepository products) {
        this.products = products;
    }

    @Transactional(readOnly = true)
    public List<ProductDto> list(Category category) {
        var entities = category == null
                ? products.findAll(Sort.by("sku"))
                : products.findByCategoryOrderByPriceAsc(category);
        return entities.stream().map(ProductDto::from).toList();
    }

    @Transactional(readOnly = true)
    public Optional<ProductDto> find(String sku) {
        return products.findById(sku).map(ProductDto::from);
    }

    @Audited
    @Transactional
    public RegisterResult register(NewProduct request) {
        if (products.existsById(request.sku())) {
            return new RegisterResult.DuplicateSku(request.sku());
        }
        var saved = products.save(new ProductEntity(
                request.sku(), request.name(), request.category(), request.price(), request.stock()));
        return new RegisterResult.Created(ProductDto.from(saved));
    }

    @Transactional(readOnly = true)
    public List<ProductDto> search(String text) {
        return products.findByNameContainingIgnoreCaseOrderByName(text).stream().map(ProductDto::from).toList();
    }
}
