package com.quanhm.ecommerce.be.service;

import com.quanhm.ecommerce.be.model.Category;
import com.quanhm.ecommerce.be.repository.CategoryRepository;
import com.quanhm.ecommerce.be.repository.ProductRepository;
import com.quanhm.ecommerce.be.request.CategoryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    // Tạo danh mục mới
    public String createCategory(String name, Long parentId) {
        return createCategory(name, null, parentId);
    }

    public String createCategory(String name, String displayName, Long parentId) {
        if (categoryRepository.existsByName(name)) {
            return "Tên danh mục '" + name + "' đã tồn tại";
        }

        Category parentCategory = null;
        if (parentId != null) {
            parentCategory = categoryRepository.findById(parentId).orElse(null);
            if (parentCategory == null) {
                return "Không tìm thấy danh mục cha có ID = " + parentId;
            }
        }

        Category category = new Category();
        category.setName(name);
        category.setDisplayName(displayName != null && !displayName.isEmpty() ? displayName : name);
        category.setParentCategory(parentCategory);
        category.setLevel(parentCategory == null ? 1 : parentCategory.getLevel() + 1);

        categoryRepository.save(category);
        return "Thêm danh mục '" + (displayName != null ? displayName : name) + "' thành công";
    }

    // Cập nhật danh mục
    public String updateCategory(Long categoryId, CategoryRequest request) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            return "Không tìm thấy danh mục có ID = " + categoryId;
        }

        // Kiểm tra nếu đổi tên, tên mới đã tồn tại chưa
        if (request.getName() != null && !request.getName().equals(category.getName())) {
            if (categoryRepository.existsByName(request.getName())) {
                return "Tên danh mục '" + request.getName() + "' đã tồn tại";
            }
            category.setName(request.getName());
        }

        // Cập nhật displayName
        if (request.getDisplayName() != null && !request.getDisplayName().isEmpty()) {
            category.setDisplayName(request.getDisplayName());
        }

        // Cập nhật parent category nếu có
        if (request.getParentName() != null) {
            if (request.getParentName().isEmpty()) {
                // Xóa parent - đưa về level 1
                category.setParentCategory(null);
                category.setLevel(1);
            } else {
                Category parentCategory = categoryRepository.findByName(request.getParentName());
                if (parentCategory == null) {
                    return "Không tìm thấy danh mục cha có tên = " + request.getParentName();
                }
                // Không cho phép set parent là chính nó hoặc con của nó
                if (parentCategory.getId().equals(categoryId)) {
                    return "Không thể đặt danh mục làm cha của chính nó";
                }
                category.setParentCategory(parentCategory);
                category.setLevel(parentCategory.getLevel() + 1);
            }
        }

        categoryRepository.save(category);
        return "Cập nhật danh mục thành công";
    }

    // Xóa danh mục
    public String deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            return "Không tìm thấy danh mục có ID = " + categoryId;
        }

        // Kiểm tra xem có danh mục con không
        List<Category> childCategories = categoryRepository.findByParentCategory(category);
        if (!childCategories.isEmpty()) {
            return "Không thể xóa danh mục này vì có " + childCategories.size() + " danh mục con";
        }

        // Kiểm tra xem có sản phẩm nào thuộc danh mục này không
        long productCount = productRepository.countByCategory(category);
        if (productCount > 0) {
            return "Không thể xóa danh mục này vì có " + productCount + " sản phẩm thuộc danh mục";
        }

        categoryRepository.delete(category);
        return "Xóa danh mục '" + category.getDisplayName() + "' thành công";
    }

    // Cập nhật tên hiển thị
    public String updateCategoryDisplayName(Long categoryId, String displayName) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            return "Không tìm thấy danh mục có ID = " + categoryId;
        }
        category.setDisplayName(displayName);
        categoryRepository.save(category);
        return "Cập nhật tên hiển thị thành công";
    }

    // Lấy tất cả danh mục
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Lấy danh mục theo ID
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }
}
