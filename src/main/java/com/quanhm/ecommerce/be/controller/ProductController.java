package com.quanhm.ecommerce.be.controller;

import com.quanhm.ecommerce.be.exception.ProductExpection;
import com.quanhm.ecommerce.be.model.Category;
import com.quanhm.ecommerce.be.model.Product;
import com.quanhm.ecommerce.be.repository.CategoryRepository;
import com.quanhm.ecommerce.be.request.CreateProductRequest;
import com.quanhm.ecommerce.be.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

//  tự động inject dependency
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryRepository categoryRepository;

    // Tìm sản phẩm theo category
    @GetMapping("/category/{categoryName}")
    public List<Product> findByCategory(@PathVariable String categoryName) {
        return productService.findProductByCategory(categoryName);
    }

    // Lấy tất cả categories có sản phẩm (dùng cho homepage)
    @GetMapping("/categories/with-products")
    public ResponseEntity<List<Category>> getCategoriesWithProducts() {
        List<Category> categories = categoryRepository.findCategoriesWithProducts();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Lấy tất cả categories theo level
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories(
            @RequestParam(required = false) Integer level) {
        List<Category> categories;
        if (level != null) {
            categories = categoryRepository.findByLevel(level);
        } else {
            categories = categoryRepository.findAll();
        }
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Lấy danh mục cho navigation menu (public API)
    @GetMapping("/categories/navigation")
    public ResponseEntity<List<Category>> getCategoriesForNavigation() {
        // Lấy tất cả categories, sắp xếp theo level
        List<Category> categories = categoryRepository.findAllByOrderByLevelAscNameAsc();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

//    Lấy danh sách sản phẩm
    @GetMapping("/products")
    public ResponseEntity<Page<Product>> findProductByCategoryHandler(
            @RequestParam String category,
            @RequestParam List<String> colors,
            @RequestParam List<String> size,
            @RequestParam Integer minPrice,
            @RequestParam Integer maxPrice,
            @RequestParam Integer minDiscount,
            @RequestParam String sort,
            @RequestParam String stock,
            @RequestParam Integer pageNumber,
            @RequestParam Integer pageSize) {

        Page<Product> res = productService.getAllProduct(
                category, colors, size, minPrice, maxPrice,
                minDiscount, sort, stock, pageNumber, pageSize
        );

        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }

//    Lấy chi tiết một sản phẩm cụ thể theo ID.
    @GetMapping("/products/{productId}")
    public ResponseEntity<Product> findProductByIdHandler(
            @PathVariable Long productId) throws ProductExpection {

        Product product = productService.findProductById(productId);

        return new ResponseEntity<>(product, HttpStatus.ACCEPTED);
    }

    // Tìm kiếm sản phẩm theo tiêu đề hoặc mô tả
    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProductHandler(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description) {

        List<Product> products = productService.searchProduct(title, description);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

//    Tạo sản phẩm
    @PostMapping("/products/add")
    public ResponseEntity<Product> createProductHandler(@RequestBody CreateProductRequest req) {
        Product createdProduct = productService.createProduct(req);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

//    Sửa sản phẩm
    @PostMapping("/products/update/{productId}")
    public ResponseEntity<Product> updateProductHandler(
            @PathVariable Long productId,
            @RequestBody CreateProductRequest req) throws ProductExpection {

        Product updatedProduct = productService.updateProduct(productId, req);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

//    Xóa sản phẩm
    @PostMapping("/products/delete/{productId}")
    public ResponseEntity<String> deleteProductHandler(@PathVariable Long productId)
            throws ProductExpection {

        String message = productService.deleteProduct(productId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }





}
