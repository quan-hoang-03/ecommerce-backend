package com.quanhm.ecommerce.be.service;

import com.quanhm.ecommerce.be.exception.OrderException;
import com.quanhm.ecommerce.be.model.*;
import com.quanhm.ecommerce.be.repository.OrderCancellationRequestRepository;
import com.quanhm.ecommerce.be.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderCancellationServiceImplementation implements OrderCancellationService {

    @Autowired
    private OrderCancellationRequestRepository cancellationRequestRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Override
    public OrderCancellationRequest requestCancellation(Long orderId, String reason, User user) throws OrderException {
        Order order = orderService.findOrderById(orderId);
        
        // Kiểm tra user có phải chủ đơn hàng không
        if (!order.getUser().getId().equals(user.getId())) {
            throw new OrderException("Bạn không có quyền hủy đơn hàng này");
        }
        
        // Kiểm tra đơn hàng có thể hủy không
        if (!canCancelOrder(order)) {
            throw new OrderException("Đơn hàng này không thể hủy. Chỉ có thể hủy đơn hàng ở trạng thái PENDING, PLACED hoặc CONFIRMED");
        }
        
        // Kiểm tra đã có request pending chưa
        Optional<OrderCancellationRequest> existingRequest = cancellationRequestRepository.findPendingRequestByOrderId(orderId);
        if (existingRequest.isPresent()) {
            throw new OrderException("Bạn đã gửi yêu cầu hủy đơn hàng này. Vui lòng chờ admin xử lý.");
        }
        
        OrderCancellationRequest request = new OrderCancellationRequest();
        request.setOrder(order);
        request.setUser(user);
        request.setReason(reason);
        request.setStatus(OrderCancellationRequest.CancellationStatus.PENDING);
        request.setRequestedAt(LocalDateTime.now());
        
        return cancellationRequestRepository.save(request);
    }

    @Override
    public List<OrderCancellationRequest> getUserCancellationRequests(Long userId) {
        return cancellationRequestRepository.findByUserIdOrderByRequestedAtDesc(userId);
    }

    @Override
    public List<OrderCancellationRequest> getPendingRequests() {
        return cancellationRequestRepository.findByStatusOrderByRequestedAtDesc(
            OrderCancellationRequest.CancellationStatus.PENDING
        );
    }

    @Override
    public OrderCancellationRequest approveRequest(Long requestId, String adminNote, User admin) throws OrderException {
        OrderCancellationRequest request = cancellationRequestRepository.findById(requestId)
            .orElseThrow(() -> new OrderException("Không tìm thấy yêu cầu hủy đơn hàng"));
        
        if (request.getStatus() != OrderCancellationRequest.CancellationStatus.PENDING) {
            throw new OrderException("Yêu cầu này đã được xử lý");
        }
        
        // Hủy đơn hàng
        Order order = request.getOrder();
        orderService.cancelOrder(order.getId());
        
        // Cập nhật request
        request.setStatus(OrderCancellationRequest.CancellationStatus.APPROVED);
        request.setProcessedAt(LocalDateTime.now());
        request.setProcessedBy(admin);
        request.setAdminNote(adminNote);
        
        return cancellationRequestRepository.save(request);
    }

    @Override
    public OrderCancellationRequest rejectRequest(Long requestId, String adminNote, User admin) throws OrderException {
        OrderCancellationRequest request = cancellationRequestRepository.findById(requestId)
            .orElseThrow(() -> new OrderException("Không tìm thấy yêu cầu hủy đơn hàng"));
        
        if (request.getStatus() != OrderCancellationRequest.CancellationStatus.PENDING) {
            throw new OrderException("Yêu cầu này đã được xử lý");
        }
        
        request.setStatus(OrderCancellationRequest.CancellationStatus.REJECTED);
        request.setProcessedAt(LocalDateTime.now());
        request.setProcessedBy(admin);
        request.setAdminNote(adminNote);
        
        return cancellationRequestRepository.save(request);
    }

    @Override
    public void deleteCancelledOrder(Long orderId, User user) throws OrderException {
        Order order = orderService.findOrderById(orderId);
        
        // Kiểm tra user có phải chủ đơn hàng không
        if (!order.getUser().getId().equals(user.getId())) {
            throw new OrderException("Bạn không có quyền xóa đơn hàng này");
        }
        
        // Chỉ cho phép xóa đơn hàng đã bị hủy
        if (!"CANCELLED".equals(order.getOrderStatus())) {
            throw new OrderException("Chỉ có thể xóa đơn hàng đã bị hủy");
        }
        
        // Kiểm tra có request approved không
        List<OrderCancellationRequest> cancellationRequests = cancellationRequestRepository.findAllByOrderId(orderId);
        boolean hasApprovedRequest = cancellationRequests.stream()
            .anyMatch(r -> r.getStatus() == OrderCancellationRequest.CancellationStatus.APPROVED);
        
        if (!hasApprovedRequest) {
            throw new OrderException("Đơn hàng này chưa được hủy bởi admin");
        }
        
        // Xóa tất cả OrderCancellationRequest liên quan trước khi xóa Order để tránh lỗi cascade
        cancellationRequestRepository.deleteAll(cancellationRequests);
        
        // Sau đó mới xóa Order
        orderRepository.deleteById(orderId);
    }

    private boolean canCancelOrder(Order order) {
        String status = order.getOrderStatus();
        return "PENDING".equals(status) || 
               "PLACED".equals(status) || 
               "CONFIRMED".equals(status);
    }
}
