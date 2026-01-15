package com.quanhm.ecommerce.be.service;

import com.quanhm.ecommerce.be.exception.ProductExpection;
import com.quanhm.ecommerce.be.model.Product;

import java.util.List;

public interface InventoryService {
    List<Product> getAllInventory();
    
    Product updateInventory(Long productId, int quantity) throws ProductExpection;
    
    Product addInventory(Long productId, int quantity) throws ProductExpection;
    
    Product reduceInventory(Long productId, int quantity) throws ProductExpection;
    
    Product getInventoryByProductId(Long productId) throws ProductExpection;
}
