package com.quanhm.ecommerce.be.service;

import com.quanhm.ecommerce.be.model.TopProductDTO;
import com.quanhm.ecommerce.be.repository.OrderItemRepository;
import com.quanhm.ecommerce.be.repository.OrderRepository;
import com.quanhm.ecommerce.be.repository.UserRepository;
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


    public List<Map<String, Object>> getMonthlySalesQuantity() {
        List<Object[]> result = orderRepository.getMonthlySalesQuantity();

        return result.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("month", "Tháng " + row[0]);
            map.put("quantity", row[1]);
            return map;
        }).collect(Collectors.toList());
    }
}
