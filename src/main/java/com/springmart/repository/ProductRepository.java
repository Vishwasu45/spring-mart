package com.springmart.repository;

import com.springmart.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    Optional<Product> findBySlug(String slug);

    Page<Product> findByIsActiveTrue(Pageable pageable);

    Page<Product> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    List<Product> findBySellerId(Long sellerId);

    @Query("SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.createdAt DESC")
    List<Product> findLatestProducts(Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN p.reviews r WHERE p.isActive = true " +
            "GROUP BY p.id ORDER BY AVG(r.rating) DESC")
    List<Product> findTopRatedProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isFeatured = true AND p.isActive = true ORDER BY p.createdAt DESC")
    List<Product> findFeaturedProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.discountPercentage > 0 AND p.isActive = true ORDER BY p.discountPercentage DESC")
    List<Product> findProductsOnSale(Pageable pageable);

    long countByCategoryIdAndIsActiveTrue(Long categoryId);

    // Recommendation queries
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.isActive = true " +
            "AND p.price BETWEEN :minPrice AND :maxPrice ORDER BY p.averageRating DESC")
    List<Product> findByCategoryAndPriceRange(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    @Query("SELECT DISTINCT p.category.id FROM Order o JOIN o.items oi JOIN oi.product p " +
            "WHERE o.user.id = :userId")
    List<Long> findCategoriesByUserOrders(@Param("userId") Long userId);

    @Query("SELECT p FROM Product p WHERE p.category.id IN :categoryIds AND p.isActive = true " +
            "ORDER BY p.averageRating DESC, p.reviewCount DESC")
    List<Product> findTopRatedByCategories(
            @Param("categoryIds") List<Long> categoryIds,
            Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.items oi JOIN oi.order o " +
            "WHERE o.createdAt > :since AND p.isActive = true " +
            "GROUP BY p.id ORDER BY COUNT(o.id) DESC")
    List<Product> findTrendingProducts(
            @Param("since") LocalDateTime since,
            Pageable pageable);
}
