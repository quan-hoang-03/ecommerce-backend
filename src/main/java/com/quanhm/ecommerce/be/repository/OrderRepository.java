package com.quanhm.ecommerce.be.repository;


import com.quanhm.ecommerce.be.model.Address;
import com.quanhm.ecommerce.be.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository <Order, Long>{
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND (o.orderStatus = 'PLACED' OR o.orderStatus = 'CONFIRMED' OR o.orderStatus = 'SHIPPED' OR o.orderStatus = 'DELIVERED')")
    public List<Order> getUsersOrders(@Param("userId") Long userId);

    // Tổng doanh thu (chỉ tính các đơn đã hoàn thành)
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.orderStatus = 'PLACED'")
    Double getTotalRevenue();

    // Thống kê tổng số lượng sản phẩm bán theo tháng
    @Query("SELECT MONTH(o.createdAt) AS month, SUM(oi.quantity) AS totalQuantity " +
            "FROM Order o JOIN o.orderItems oi " +
            "WHERE o.orderStatus = 'DELIVERED' " +   // hoặc 'DELIVERED'
            "GROUP BY MONTH(o.createdAt) " +
            "ORDER BY MONTH(o.createdAt)")
    List<Object[]> getMonthlySalesQuantity();

    List<Order> findByShippingAddress(Address address);

}
