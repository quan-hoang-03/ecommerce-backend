package com.quanhm.ecommerce.be.controller;

import com.quanhm.ecommerce.be.exception.OrderException;
import com.quanhm.ecommerce.be.exception.UserException;
import com.quanhm.ecommerce.be.model.OrderCancellationRequest;
import com.quanhm.ecommerce.be.model.User;
import com.quanhm.ecommerce.be.service.OrderCancellationService;
import com.quanhm.ecommerce.be.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderCancellationController {

    @Autowired
    private OrderCancellationService cancellationService;

    @Autowired
    private UserService userService;

    // User: Gửi yêu cầu hủy đơn hàng
    @PostMapping("/{orderId}/cancel-request")
    public ResponseEntity<OrderCancellationRequest> requestCancellation(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String jwt) throws UserException, OrderException {
        User user = userService.findUserProfileByJwt(jwt);
        String reason = request.get("reason");
        OrderCancellationRequest cancellationRequest = cancellationService.requestCancellation(orderId, reason, user);
        return new ResponseEntity<>(cancellationRequest, HttpStatus.CREATED);
    }

    // User: Xem danh sách yêu cầu hủy của mình
    @GetMapping("/cancel-requests")
    public ResponseEntity<List<OrderCancellationRequest>> getUserCancellationRequests(
            @RequestHeader("Authorization") String jwt) throws UserException {
        User user = userService.findUserProfileByJwt(jwt);
        List<OrderCancellationRequest> requests = cancellationService.getUserCancellationRequests(user.getId());
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    // User: Xóa đơn hàng đã hủy
    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteCancelledOrder(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String jwt) throws UserException, OrderException {
        User user = userService.findUserProfileByJwt(jwt);
        cancellationService.deleteCancelledOrder(orderId, user);
        return new ResponseEntity<>("Đã xóa đơn hàng thành công", HttpStatus.OK);
    }

    // Admin: Xem tất cả yêu cầu hủy đang chờ
    @GetMapping("/admin/cancel-requests/pending")
    public ResponseEntity<List<OrderCancellationRequest>> getPendingRequests(
            @RequestHeader("Authorization") String jwt) throws UserException {
        User admin = userService.findUserProfileByJwt(jwt);
        // TODO: Kiểm tra quyền admin
        List<OrderCancellationRequest> requests = cancellationService.getPendingRequests();
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    // Admin: Duyệt yêu cầu hủy
    @PutMapping("/admin/cancel-requests/{requestId}/approve")
    public ResponseEntity<OrderCancellationRequest> approveRequest(
            @PathVariable Long requestId,
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String jwt) throws UserException, OrderException {
        User admin = userService.findUserProfileByJwt(jwt);
        // TODO: Kiểm tra quyền admin
        String adminNote = request.get("adminNote");
        OrderCancellationRequest approvedRequest = cancellationService.approveRequest(requestId, adminNote, admin);
        return new ResponseEntity<>(approvedRequest, HttpStatus.OK);
    }

    // Admin: Từ chối yêu cầu hủy
    @PutMapping("/admin/cancel-requests/{requestId}/reject")
    public ResponseEntity<OrderCancellationRequest> rejectRequest(
            @PathVariable Long requestId,
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String jwt) throws UserException, OrderException {
        User admin = userService.findUserProfileByJwt(jwt);
        // TODO: Kiểm tra quyền admin
        String adminNote = request.get("adminNote");
        OrderCancellationRequest rejectedRequest = cancellationService.rejectRequest(requestId, adminNote, admin);
        return new ResponseEntity<>(rejectedRequest, HttpStatus.OK);
    }
}
