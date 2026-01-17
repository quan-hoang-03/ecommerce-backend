package com.quanhm.ecommerce.be.controller;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import com.quanhm.ecommerce.be.exception.OrderException;
import com.quanhm.ecommerce.be.model.Order;
import com.quanhm.ecommerce.be.repository.OrderRepository;
import com.quanhm.ecommerce.be.service.OrderService;
import com.quanhm.ecommerce.be.service.PayPalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/paypal")
public class PaymentController {

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    @Autowired
    private PayPalService payPalService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    // ✅ 1️⃣ Tạo link thanh toán
    @PostMapping("/payments/{orderId}")
    public ResponseEntity<?> createPaymentLink(@PathVariable Long orderId) {
        try {
            Order order = orderService.findOrderById(orderId);
            double amount = order.getTotalPrice();
            
            // Log để debug
            System.out.println("Creating PayPal payment for order: " + orderId + ", amount: " + amount);
            
            // Kiểm tra amount hợp lệ
            if (amount <= 0) {
                return ResponseEntity.badRequest().body("Số tiền không hợp lệ: " + amount);
            }

            String returnUrl = "http://localhost:3000/payment/success/" + orderId;
            String cancelUrl = "http://localhost:3000/payment/cancel/" + orderId;

            String approvalUrl = payPalService.createOrder(amount, returnUrl, cancelUrl);

            PaymentListResponse res = new PaymentListResponse();
            res.setPayment_link_url(approvalUrl);
            res.setPayment_link_id("paypal-" + orderId);

            return new ResponseEntity<>(res, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("PayPal payment error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi tạo thanh toán: " + e.getMessage());
        }
    }

    // ✅ 2️⃣ Capture thanh toán sau khi người dùng trả tiền xong
    @GetMapping("/capture/{paypalOrderId}/{orderId}")
    public ResponseEntity<String> capturePayment(@PathVariable String paypalOrderId,
                                                 @PathVariable Long orderId)
            throws IOException, OrderException {

        PayPalEnvironment environment = "live".equalsIgnoreCase(mode)
                ? new PayPalEnvironment.Live(clientId, clientSecret)
                : new PayPalEnvironment.Sandbox(clientId, clientSecret);

        PayPalHttpClient client = new PayPalHttpClient(environment);

        OrdersCaptureRequest request = new OrdersCaptureRequest(paypalOrderId);
        request.requestBody(new OrderRequest());

        HttpResponse<com.paypal.orders.Order> response = client.execute(request);
        com.paypal.orders.Order captureOrderResponse = response.result();

        if ("COMPLETED".equals(captureOrderResponse.status())) {
            Order order = orderService.findOrderById(orderId);
            order.setOrderStatus("PLACED");
            order.getPaymentDetails().setPaymentStatus("COMPLETED");
            order.setPaypalOrderId(captureOrderResponse.id());
            orderRepository.save(order);
        }

        return ResponseEntity.ok("Payment captured successfully");
    }
}
