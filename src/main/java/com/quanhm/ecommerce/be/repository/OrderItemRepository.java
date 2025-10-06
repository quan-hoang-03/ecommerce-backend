package com.quanhm.ecommerce.be.repository;

import com.quanhm.ecommerce.be.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository <OrderItem,Long> {

}
