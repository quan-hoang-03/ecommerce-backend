package com.quanhm.ecommerce.be.model;

import java.lang.Double;

public class TopProductDTO {
    private Long productId;
    private String productName;
    private Long quantitySold;
    private Double totalRevenue;

    public TopProductDTO(Long productId, String productName, Long quantitySold, Double totalRevenue) {
        this.productId = productId;
        this.productName = productName;
        this.quantitySold = quantitySold;
        this.totalRevenue = totalRevenue;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(Long quantitySold) {
        this.quantitySold = quantitySold;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
