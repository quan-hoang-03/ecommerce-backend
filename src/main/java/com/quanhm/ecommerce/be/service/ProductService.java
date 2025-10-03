package com.quanhm.ecommerce.be.service;


import com.quanhm.ecommerce.be.exception.ProductExpection;
import com.quanhm.ecommerce.be.model.Product;
import com.quanhm.ecommerce.be.request.CreateProductRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    public Product createProduct(CreateProductRequest req);

    public String deleteProduct(Long productId) throws ProductExpection;

    public Product updateProduct(Long productId, Product req) throws ProductExpection;

    public Product findProductById(Long id) throws ProductExpection;

    public List<Product> findProductByCategory(String category);

    public Page<Product> getAllProduct(String category, List<String>colors, List<String>sizes,
                                       Integer minPrice, Integer maxPrice, Integer page,
                                       Integer minDiscount,String sort,String stock,
                                       Integer pageNumber, Integer pageSize);
}
