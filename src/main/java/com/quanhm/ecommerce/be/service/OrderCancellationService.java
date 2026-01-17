package com.quanhm.ecommerce.be.service;

import com.quanhm.ecommerce.be.exception.OrderException;
import com.quanhm.ecommerce.be.model.OrderCancellationRequest;
import com.quanhm.ecommerce.be.model.User;

import java.util.List;

public interface OrderCancellationService {
    OrderCancellationRequest requestCancellation(Long orderId, String reason, User user) throws OrderException;
    
    List<OrderCancellationRequest> getUserCancellationRequests(Long userId);
    
    List<OrderCancellationRequest> getPendingRequests();
    
    OrderCancellationRequest approveRequest(Long requestId, String adminNote, User admin) throws OrderException;
    
    OrderCancellationRequest rejectRequest(Long requestId, String adminNote, User admin) throws OrderException;
    
    void deleteCancelledOrder(Long orderId, User user) throws OrderException;
}
