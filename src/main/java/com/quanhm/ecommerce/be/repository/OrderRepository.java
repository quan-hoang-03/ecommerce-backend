package com.quanhm.ecommerce.be.repository;


import com.quanhm.ecommerce.be.model.Address;
import com.quanhm.ecommerce.be.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository <Order, Long>{
    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.orderItems oi " +
           "LEFT JOIN FETCH oi.product p " +
           "LEFT JOIN FETCH o.shippingAddress " +
           "WHERE o.user.id = :userId " +
           "ORDER BY o.orderDate DESC")
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

    // Thống kê doanh số và số đơn hàng theo tháng (cho biểu đồ)
    @Query("SELECT MONTH(o.orderDate) AS month, " +
            "COALESCE(SUM(o.totalPrice), 0) AS sales, " +
            "COUNT(o.id) AS orders " +
            "FROM Order o " +
            "WHERE o.orderStatus IN ('PLACED', 'CONFIRMED', 'SHIPPED', 'DELIVERED') " +
            "AND YEAR(o.orderDate) = YEAR(CURRENT_DATE) " +
            "GROUP BY MONTH(o.orderDate) " +
            "ORDER BY MONTH(o.orderDate)")
    List<Object[]> getMonthlySalesAndOrders();

    List<Order> findByShippingAddress(Address address);

}
