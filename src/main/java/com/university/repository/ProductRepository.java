package com.university.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.university.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Derived query methods — Spring generates the SQL from the method name

    List<Product> findByCategory(String category);

    List<Product> findByCategory(String category, Sort sort);

    List<Product> findByNameContaining(String keyword);

    List<Product> findByNameContaining(String keyword, Sort sort);

    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);

    List<Product> findByCategoryOrderByPriceAsc(String category);

    boolean existsByProductCode(String productCode);

    long countByCategory(String category);
    // JPQL query — explicit when method naming is insufficient

    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category")
    List<String> findAllCategories();

    // Advanced search with multiple optional parameters
    @Query("SELECT p FROM Product p WHERE "
            + "(:name IS NULL OR p.name LIKE %:name%) AND "
            + "(:category IS NULL OR p.category = :category) AND "
            + "(:minPrice IS NULL OR p.price >= :minPrice) AND "
            + "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Product> advancedSearch(
            @Param("name") String name,
            @Param("category") String category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Sort sort);

    // Statistics queries
    @Query("SELECT COALESCE(p.category, 'Uncategorized'), COUNT(p) FROM Product p GROUP BY p.category ORDER BY p.category")
    List<Object[]> countProductsByCategory();

    @Query("SELECT COALESCE(SUM(p.price * p.quantity), 0) FROM Product p")
    BigDecimal calculateTotalInventoryValue();

    List<Product> findByQuantityLessThanOrderByQuantityAsc(Integer threshold);
}
