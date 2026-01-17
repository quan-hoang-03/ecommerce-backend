package com.quanhm.ecommerce.be.repository;

import com.quanhm.ecommerce.be.model.OrderCancellationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderCancellationRequestRepository extends JpaRepository<OrderCancellationRequest, Long> {
    
    List<OrderCancellationRequest> findByUserIdOrderByRequestedAtDesc(Long userId);
    
    List<OrderCancellationRequest> findByStatusOrderByRequestedAtDesc(OrderCancellationRequest.CancellationStatus status);
    
    Optional<OrderCancellationRequest> findByOrderId(Long orderId);
    
    List<OrderCancellationRequest> findAllByOrderId(Long orderId);
    
    @Query("SELECT r FROM OrderCancellationRequest r WHERE r.order.id = :orderId AND r.status = 'PENDING'")
    Optional<OrderCancellationRequest> findPendingRequestByOrderId(@Param("orderId") Long orderId);
}
