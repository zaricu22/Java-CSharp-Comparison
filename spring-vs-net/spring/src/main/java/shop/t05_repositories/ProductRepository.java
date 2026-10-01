// VERDICT | T05 Repositories | BETTER: SPRING
// WHY: Spring Data derives queries from method names with zero implementation; EF's DbSet is already a repository, but every query is written as LINQ.

package shop.t05_repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.domain.Category;
import shop.domain.ProductEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * No implementation class exists. findAll/findById/save/delete/count come from JpaRepository,
 * and every method below is turned into SQL from its NAME at startup (typos fail the boot).
 */
public interface ProductRepository extends JpaRepository<ProductEntity, String> {

    List<ProductEntity> findByCategoryOrderByPriceAsc(Category category);

    List<ProductEntity> findByPriceLessThanAndStockGreaterThanOrderByPriceAsc(BigDecimal maxPrice, int minStock);

    List<ProductEntity> findByNameContainingIgnoreCaseOrderByName(String text);

    long countByStockLessThan(int threshold);

    /** T17: add a Pageable parameter and the same derived query becomes paged + sorted. */
    Page<ProductEntity> findByCategory(Category category, Pageable pageable);
}
