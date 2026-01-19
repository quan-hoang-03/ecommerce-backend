package com.quanhm.ecommerce.be.repository;
import com.quanhm.ecommerce.be.model.Category;
import com.quanhm.ecommerce.be.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Đếm số sản phẩm theo category
    long countByCategory(Category category);
    @Query("SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.category c " +
            "LEFT JOIN FETCH c.parentCategory pc " +
            "LEFT JOIN FETCH pc.parentCategory ppc " +
            "WHERE (:category = '' OR " +
            "       LOWER(c.name) = LOWER(:category) OR " +
            "       (pc IS NOT NULL AND LOWER(pc.name) = LOWER(:category)) OR " +
            "       (pc IS NOT NULL AND ppc IS NOT NULL AND LOWER(ppc.name) = LOWER(:category))) " +
            "AND ((:minPrice = '0' AND :maxPrice = '0') OR (p.discountPrice BETWEEN CAST(:minPrice AS int) AND CAST(:maxPrice AS int))) " +
            "AND (:minDiscount = '0' OR p.discountPersent >= CAST(:minDiscount AS int)) " +
            "ORDER BY " +
            "CASE WHEN :sort = 'price_low' THEN p.discountPrice END ASC, " +
            "CASE WHEN :sort = 'price_high' THEN p.discountPrice END DESC")


    public List<Product>filterProducts(@Param("category") String category,
                                       @Param("minPrice") String minPrice,
                                       @Param("maxPrice") String maxPrice,
                                       @Param("minDiscount") String minDiscount,
                                       @Param("sort") String sort);

    @Query("SELECT p FROM Product p WHERE LOWER(p.category.name) = LOWER(:category)")
    List<Product> findProductByCategory(@Param("category") String category);


    List<Product> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);

    List<Product> findByTitleContainingIgnoreCase(String title);

    List<Product> findByDescriptionContainingIgnoreCase(String description);

    List<Product> findByCategory_NameIgnoreCase(String categoryName);

    // Fetch category khi lấy tất cả products
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.category")
    List<Product> findAllWithCategory();

}
