package com.quanhm.ecommerce.be.controller;

import com.quanhm.ecommerce.be.model.Category;
import com.quanhm.ecommerce.be.repository.CategoryRepository;
import com.quanhm.ecommerce.be.request.CategoryRequest;
import com.quanhm.ecommerce.be.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping("/add")
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequest request) {
        Long parentId = null;
        if (request.getParentName() != null && !request.getParentName().isEmpty()) {
            Category parentCategory = categoryRepository.findByName(request.getParentName());
            if (parentCategory != null) {
                parentId = parentCategory.getId();
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Không tìm thấy danh mục cha có tên = " + request.getParentName()));
            }
        }

        String message = categoryService.createCategory(request.getName(), parentId);

        if (message.contains("không tìm thấy") || message.contains("đã tồn tại")) {
            return ResponseEntity.badRequest().body(Map.of("message", message));
        }

        return ResponseEntity.ok(Map.of("message", message));
    }


}
