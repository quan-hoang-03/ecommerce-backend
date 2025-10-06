package com.quanhm.ecommerce.be.service;

import com.quanhm.ecommerce.be.model.OrderItem;
import com.quanhm.ecommerce.be.repository.OrderItemRepository;

public class OrderItemServiceImpletation implements OrderItemService{


    private OrderItemRepository orderItemRepository;

    public OrderItemServiceImpletation(OrderItemRepository orderItemRepository){
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public OrderItem createOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }
}
