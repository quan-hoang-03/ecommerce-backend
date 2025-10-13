package com.quanhm.ecommerce.be.controller;

import com.quanhm.ecommerce.be.exception.ProductExpection;
import com.quanhm.ecommerce.be.model.Product;
import com.quanhm.ecommerce.be.request.CreateProductRequest;
import com.quanhm.ecommerce.be.response.ApiResponse;
import com.quanhm.ecommerce.be.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/")
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequest req){
        Product product = productService.createProduct(req);
        return new ResponseEntity<Product>(product, HttpStatus.CREATED);
    }

    @PostMapping("/{productId}/delete")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId) throws ProductExpection {
        productService.deleteProduct(productId);
        ApiResponse res = new ApiResponse();
        res.setMessage("Xóa sản phẩm thành công");
        res.setSuccess(true);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

//    @GetMapping("/all")
//    public ResponseEntity<List<Product>> findAllProducts(){
//        List<Product> products = productService.findAllProducts();
//        return  new ResponseEntity<>(products, HttpStatus.OK);
//    }

    @PostMapping("/{productId}/update")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId, @RequestBody Product req) throws ProductExpection {
        Product product = productService.updateProduct(productId, req);
        return new ResponseEntity<Product>(product, HttpStatus.CREATED);
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
