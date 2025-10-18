package com.quanhm.ecommerce.be.repository;

import com.quanhm.ecommerce.be.model.OrderItem;
import com.quanhm.ecommerce.be.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface OrderItemRepository extends JpaRepository <OrderItem,Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM OrderItem oi WHERE oi.product.id = :productId")
    void deleteByProductOrder(@Param("productId") Long productId);

}
