package com.quanhm.ecommerce.be.service;

import com.quanhm.ecommerce.be.model.Category;
import com.quanhm.ecommerce.be.repository.CategoryRepository;
import com.quanhm.ecommerce.be.request.CategoryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public String createCategory(String name, Long parentId) {
        if (categoryRepository.existsByName(name)) {
            return "Tên danh mục '" + name + "' đã tồn tại";
        }

        Category parentCategory = null;
        if (parentId != null) {
            parentCategory = categoryRepository.findById(parentId)
                    .orElse(null);
            if (parentCategory == null) {
                return "Không tìm thấy danh mục cha có ID = " + parentId;
            }
        }

        Category category = new Category();
        category.setName(name);
        category.setParentCategory(parentCategory);
        category.setLevel(parentCategory == null ? 1 : parentCategory.getLevel() + 1);

        categoryRepository.save(category);
        return "Thêm danh mục '" + name + "' thành công";
    }


}
