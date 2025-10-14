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

import com.quanhm.ecommerce.be.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // 🧩 Tạo PayPal Client theo môi trường
    private PayPalHttpClient payPalClient() {
        PayPalEnvironment environment = "sandbox".equalsIgnoreCase(mode)
                ? new PayPalEnvironment.Sandbox(clientId, clientSecret)
                : new PayPalEnvironment.Live(clientId, clientSecret);
        return new PayPalHttpClient(environment);
    }

    @PostMapping("/payments/{orderId}")
    public ResponseEntity<PaymentListResponse> createPaymentLink(@PathVariable Long orderId) throws IOException, OrderException {
        Order order = orderService.findOrderById(orderId);

        double amount = order.getTotalPrice();

        String returnUrl = "http://localhost:3000/payment/success/" + orderId;
        String cancelUrl = "http://localhost:3000/payment/cancel/" + orderId;

        String approvalUrl = payPalService.createOrder(amount, returnUrl, cancelUrl);

        PaymentListResponse res = new PaymentListResponse();
        res.setPayment_link_url(approvalUrl);
        res.setPayment_link_id("paypal-" + orderId);

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }
}
