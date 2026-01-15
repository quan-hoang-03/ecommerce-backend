package com.quanhm.ecommerce.be.controller;

import com.quanhm.ecommerce.be.exception.ProductExpection;
import com.quanhm.ecommerce.be.model.Product;
import com.quanhm.ecommerce.be.response.ApiResponse;
import com.quanhm.ecommerce.be.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/")
    public ResponseEntity<List<Product>> getAllInventory(@RequestHeader("Authorization") String jwt) {
        List<Product> products = inventoryService.getAllInventory();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getInventoryByProductId(
            @PathVariable Long productId,
            @RequestHeader("Authorization") String jwt) throws ProductExpection {
        Product product = inventoryService.getInventoryByProductId(productId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateInventory(
            @PathVariable Long productId,
            @RequestParam int quantity,
            @RequestHeader("Authorization") String jwt) throws ProductExpection {
        Product product = inventoryService.updateInventory(productId, quantity);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping("/{productId}/add")
    public ResponseEntity<Product> addInventory(
            @PathVariable Long productId,
            @RequestParam int quantity,
            @RequestHeader("Authorization") String jwt) throws ProductExpection {
        Product product = inventoryService.addInventory(productId, quantity);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping("/{productId}/reduce")
    public ResponseEntity<Product> reduceInventory(
            @PathVariable Long productId,
            @RequestParam int quantity,
            @RequestHeader("Authorization") String jwt) throws ProductExpection {
        Product product = inventoryService.reduceInventory(productId, quantity);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse> deleteInventory(
            @PathVariable Long productId,
            @RequestHeader("Authorization") String jwt) throws ProductExpection {
        inventoryService.updateInventory(productId, 0);
        ApiResponse res = new ApiResponse();
        res.setMessage("Đã xóa số lượng trong kho thành công");
        res.setSuccess(true);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
