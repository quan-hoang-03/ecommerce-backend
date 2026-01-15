package com.quanhm.ecommerce.be.service;

import com.quanhm.ecommerce.be.exception.ProductExpection;
import com.quanhm.ecommerce.be.model.Product;
import com.quanhm.ecommerce.be.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryServiceImplementation implements InventoryService {

    private ProductRepository productRepository;

    public InventoryServiceImplementation(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllInventory() {
        return productRepository.findAll();
    }

    @Override
    public Product updateInventory(Long productId, int quantity) throws ProductExpection {
        Optional<Product> opt = productRepository.findById(productId);
        
        if (opt.isEmpty()) {
            throw new ProductExpection("Không tìm thấy sản phẩm với ID: " + productId);
        }
        
        Product product = opt.get();
        product.setQuantity(quantity);
        return productRepository.save(product);
    }

    @Override
    public Product addInventory(Long productId, int quantity) throws ProductExpection {
        Optional<Product> opt = productRepository.findById(productId);
        
        if (opt.isEmpty()) {
            throw new ProductExpection("Không tìm thấy sản phẩm với ID: " + productId);
        }
        
        Product product = opt.get();
        product.setQuantity(product.getQuantity() + quantity);
        return productRepository.save(product);
    }

    @Override
    public Product reduceInventory(Long productId, int quantity) throws ProductExpection {
        Optional<Product> opt = productRepository.findById(productId);
        
        if (opt.isEmpty()) {
            throw new ProductExpection("Không tìm thấy sản phẩm với ID: " + productId);
        }
        
        Product product = opt.get();
        int newQuantity = product.getQuantity() - quantity;
        
        if (newQuantity < 0) {
            throw new ProductExpection("Số lượng trong kho không đủ! Hiện có: " + product.getQuantity());
        }
        
        product.setQuantity(newQuantity);
        return productRepository.save(product);
    }

    @Override
    public Product getInventoryByProductId(Long productId) throws ProductExpection {
        Optional<Product> opt = productRepository.findById(productId);
        
        if (opt.isEmpty()) {
            throw new ProductExpection("Không tìm thấy sản phẩm với ID: " + productId);
        }
        
        return opt.get();
    }
}
