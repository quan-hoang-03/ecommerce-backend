package com.quanhm.ecommerce.be.repository;

import com.quanhm.ecommerce.be.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    
    Category findByName(String name);

    @Query("SELECT c FROM Category c WHERE c.name = :name AND c.parentCategory.name = :parentCategoryName")
    Category findByNameAndParent(@Param("name") String name, @Param("parentCategoryName") String parentCategoryName);

    // Find categories by level (1 = top level, 2 = second level, 3 = third level)
    List<Category> findByLevel(int level);

    // Find child categories
    List<Category> findByParentCategory(Category parentCategory);

    // Find categories that have products
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL")
    List<Category> findCategoriesWithProducts();

    // Find all categories ordered by level and name
    List<Category> findAllByOrderByLevelAscNameAsc();
}
