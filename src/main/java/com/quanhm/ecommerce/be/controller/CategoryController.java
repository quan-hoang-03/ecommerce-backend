package com.quanhm.ecommerce.be.controller;

import com.quanhm.ecommerce.be.model.Category;
import com.quanhm.ecommerce.be.repository.CategoryRepository;
import com.quanhm.ecommerce.be.request.CategoryRequest;
import com.quanhm.ecommerce.be.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    // Lấy tất cả danh mục
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // Lấy danh mục theo ID
    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        if (category == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Không tìm thấy danh mục có ID = " + categoryId));
        }
        return ResponseEntity.ok(category);
    }

    // Lấy danh mục theo level
    @GetMapping("/level/{level}")
    public ResponseEntity<List<Category>> getCategoriesByLevel(@PathVariable int level) {
        List<Category> categories = categoryRepository.findByLevel(level);
        return ResponseEntity.ok(categories);
    }

    // Tạo danh mục mới
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequest request) {
        Long parentId = null;
        if (request.getParentName() != null && !request.getParentName().isEmpty()) {
            Category parentCategory = categoryRepository.findByName(request.getParentName());
            if (parentCategory != null) {
                parentId = parentCategory.getId();
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Không tìm thấy danh mục cha có tên = " + request.getParentName()));
            }
        }

        String message = categoryService.createCategory(
                request.getName(),
                request.getDisplayName(),
                parentId
        );

        if (message.contains("không tìm thấy") || message.contains("đã tồn tại")) {
            return ResponseEntity.badRequest().body(Map.of("message", message));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", message));
    }

    // Cập nhật danh mục
    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody CategoryRequest request) {
        
        String message = categoryService.updateCategory(categoryId, request);

        if (message.contains("Không tìm thấy") || message.contains("đã tồn tại")) {
            return ResponseEntity.badRequest().body(Map.of("message", message));
        }

        return ResponseEntity.ok(Map.of("message", message));
    }

    // Xóa danh mục
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId) {
        String message = categoryService.deleteCategory(categoryId);

        if (message.contains("Không tìm thấy") || message.contains("không thể xóa")) {
            return ResponseEntity.badRequest().body(Map.of("message", message));
        }

        return ResponseEntity.ok(Map.of("message", message));
    }
}
