// VERDICT | T05 Repositories | BETTER: SPRING
// WHY: Spring Data derives queries from method names with zero implementation; EF's DbSet is already a repository, but every query is written as LINQ.

package shop.t05_repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import shop.domain.Category;
import shop.domain.ProductEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Test slice (T15): only JPA + an embedded database start; each test runs in a rolled-back transaction. */
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    ProductRepository products;

    @BeforeEach
    void seed() {
        products.saveAll(List.of(
                new ProductEntity("B1", "Clean Code", Category.BOOKS, new BigDecimal("35.50"), 12),
                new ProductEntity("B2", "Refactoring", Category.BOOKS, new BigDecimal("42.00"), 7),
                new ProductEntity("E1", "Keyboard", Category.ELECTRONICS, new BigDecimal("89.99"), 3),
                new ProductEntity("E2", "Monitor", Category.ELECTRONICS, new BigDecimal("249.00"), 0),
                new ProductEntity("G1", "Coffee", Category.GROCERY, new BigDecimal("12.40"), 40)));
    }

    @Test
    void crudMethodsComeForFree() {
        assertThat(products.count()).isEqualTo(5);
        assertThat(products.findById("E1")).map(ProductEntity::getName).contains("Keyboard");
        products.deleteById("E2");
        assertThat(products.existsById("E2")).isFalse();
    }

    @Test
    void derivedQueriesFromMethodNames() {
        assertThat(products.findByCategoryOrderByPriceAsc(Category.BOOKS))
                .extracting(ProductEntity::getSku).containsExactly("B1", "B2");
        assertThat(products.findByPriceLessThanAndStockGreaterThanOrderByPriceAsc(new BigDecimal("50"), 0))
                .extracting(ProductEntity::getSku).containsExactly("G1", "B1", "B2");
        assertThat(products.findByNameContainingIgnoreCaseOrderByName("O"))
                .extracting(ProductEntity::getName).containsExactly("Clean Code", "Coffee", "Keyboard", "Monitor", "Refactoring");
        assertThat(products.countByStockLessThan(5)).isEqualTo(2);
    }
}
