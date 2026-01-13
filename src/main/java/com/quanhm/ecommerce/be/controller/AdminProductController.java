package com.quanhm.ecommerce.be.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quanhm.ecommerce.be.exception.ProductExpection;
import com.quanhm.ecommerce.be.model.Product;
import com.quanhm.ecommerce.be.model.Size;
import com.quanhm.ecommerce.be.request.CreateProductRequest;
import com.quanhm.ecommerce.be.response.ApiResponse;
import com.quanhm.ecommerce.be.service.ProductService;
import com.quanhm.ecommerce.be.service.ProductServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductServiceImplementation productServiceImplementation;

    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> createProduct(
            @RequestPart("product") CreateProductRequest req,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) throws IOException {

        // Nếu có ảnh thì lưu file
        if (imageFile != null && !imageFile.isEmpty()) {
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Gán link ảnh cho product
            req.setImageUrl("/uploads/" + fileName);
        }

        Product product = productService.createProduct(req);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }


    @PostMapping("/{productId}/delete")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId) throws ProductExpection {
        productService.deleteProduct(productId);
        ApiResponse res = new ApiResponse();
        res.setMessage("Xóa sản phẩm thành công");
        res.setSuccess(true);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Product>> findAllProducts(){
        List<Product> products = productServiceImplementation.findAllProducts();
        return  new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long productId,
            @RequestPart("product") CreateProductRequest req,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) throws IOException, ProductExpection {

        // Lấy sản phẩm hiện tại
        Product existingProduct = productService.findProductById(productId);
        
        // Nếu có ảnh mới thì lưu file và xóa ảnh cũ
        if (imageFile != null && !imageFile.isEmpty()) {
            // Validate file type
            String contentType = imageFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new ProductExpection("File phải là hình ảnh");
            }
            
            // Xóa ảnh cũ nếu có
            if (existingProduct.getImageUrl() != null && !existingProduct.getImageUrl().isEmpty()) {
                try {
                    String oldFileName = existingProduct.getImageUrl().replace("/uploads/", "");
                    Path oldFilePath = Paths.get("uploads").resolve(oldFileName);
                    if (Files.exists(oldFilePath)) {
                        Files.delete(oldFilePath);
                    }
                } catch (Exception e) {
                    // Bỏ qua lỗi nếu không xóa được file cũ
                    System.err.println("Không thể xóa ảnh cũ: " + e.getMessage());
                }
            }

            // Lưu ảnh mới
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Gán link ảnh mới cho product
            req.setImageUrl("/uploads/" + fileName);
        } else {
            // Nếu không có ảnh mới, giữ nguyên ảnh cũ
            if (existingProduct.getImageUrl() != null) {
                req.setImageUrl(existingProduct.getImageUrl());
            }
        }

        Product product = productService.updateProduct(productId, req);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping("/creates")
    public ResponseEntity<ApiResponse> createMultipleProduct(@RequestBody CreateProductRequest[] req) {
        for(CreateProductRequest product:req){
            productService.createProduct(product);
        }
        ApiResponse res = new ApiResponse();
        res.setMessage("Thêm sản phẩm thành công");
        res.setSuccess(true);
        return new ResponseEntity<>(res,HttpStatus.CREATED);
    }
}
