package com.quanhm.ecommerce.be.controller;

import com.quanhm.ecommerce.be.service.AdminStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminStatisticsController {

    @Autowired
    private AdminStatisticsService statisticsService;

    @GetMapping("/sales-statistics")
    public ResponseEntity<?> getMonthlySalesQuantity() {
        return ResponseEntity.ok(statisticsService.getMonthlySalesQuantity());
    }
}
