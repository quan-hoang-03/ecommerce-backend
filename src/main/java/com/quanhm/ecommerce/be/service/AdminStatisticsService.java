package com.quanhm.ecommerce.be.service;

import com.quanhm.ecommerce.be.model.TopProductDTO;
import com.quanhm.ecommerce.be.repository.OrderItemRepository;
import com.quanhm.ecommerce.be.repository.OrderRepository;
import com.quanhm.ecommerce.be.repository.UserRepository;
import com.quanhm.ecommerce.be.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminStatisticsService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<Map<String, Object>> getMonthlySalesQuantity() {
        List<Object[]> result = orderRepository.getMonthlySalesQuantity();

        return result.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("month", "Tháng " + row[0]);
            map.put("quantity", row[1]);
            return map;
        }).collect(Collectors.toList());
    }

    // Tổng số khách hàng
    public Long getTotalCustomers() {
        return userRepository.count();
    }

    // Tổng số sản phẩm
    public Long getTotalProducts() {
        return productRepository.count();
    }

    // Tổng doanh thu (tổng tiền từ các đơn hàng đã đặt)
    public Double getTotalRevenue() {
        Double revenue = orderRepository.getTotalRevenue();
        return revenue != null ? revenue : 0.0;
    }

    // Tổng doanh số (tổng số lượng hàng bán được)
    public Long getTotalSalesQuantity() {
        Long quantity = orderItemRepository.getTotalSalesQuantity();
        return quantity != null ? quantity : 0L;
    }

    // Tổng giá trị đơn hàng (tổng giá trị các đơn đã đặt)
    public Double getTotalSalesValue() {
        Double value = orderRepository.getTotalRevenue();
        return value != null ? value : 0.0;
    }

    // Lấy tất cả thống kê tổng quan
    public Map<String, Object> getOverviewStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCustomers", getTotalCustomers());
        stats.put("totalProducts", getTotalProducts());
        stats.put("totalRevenue", getTotalRevenue());
        stats.put("totalSalesQuantity", getTotalSalesQuantity());
        stats.put("totalSalesValue", getTotalSalesValue());
        return stats;
    }
}
