// VERDICT | T17 Pagination & sorting | BETTER: SPRING
// WHY: a Pageable parameter gives page/size/sort binding, defaults, a max page size and a Page with totals from the repository; ASP.NET writes Skip/Take, the count query, sort parsing and the page DTO by hand.

package shop.t17_pagination;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.domain.Category;
import shop.domain.ProductEntity;
import shop.t01_endpoints.ProductDto;
import shop.t05_repositories.ProductRepository;

/**
 * GET /api/products/paged?page=1&size=2&sort=price,desc
 * Spring Data binds page, size and sort (several sort params allowed), applies the defaults below,
 * caps the size (2000 by default), runs the page query AND the count query, and returns the totals.
 */
@RestController
public class ProductPageController {

    private final ProductRepository products;

    public ProductPageController(ProductRepository products) {
        this.products = products;
    }

    @GetMapping("/api/products/paged")
    @Transactional(readOnly = true)
    public PagedModel<ProductDto> page(@RequestParam(required = false) Category category,
                                       @PageableDefault(size = 20, sort = "sku") Pageable pageable) {
        Page<ProductEntity> page = category == null
                ? products.findAll(pageable)
                : products.findByCategory(category, pageable);
        return new PagedModel<>(page.map(ProductDto::from)); // JSON: { content: [...], page: { size, number, totalElements, totalPages } }
    }
}
