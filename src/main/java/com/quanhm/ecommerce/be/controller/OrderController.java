package com.quanhm.ecommerce.be.controller;


import com.quanhm.ecommerce.be.exception.OrderException;
import com.quanhm.ecommerce.be.exception.UserException;
import com.quanhm.ecommerce.be.model.Address;
import com.quanhm.ecommerce.be.model.Order;
import com.quanhm.ecommerce.be.model.User;
import com.quanhm.ecommerce.be.service.OrderService;
import com.quanhm.ecommerce.be.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public ResponseEntity<Order> createOrder(@RequestBody Address shippingAddress, @RequestHeader("Authorization") String jwt) throws UserException {
        User user = userService.findUserProfileByJwt(jwt);
        Order orders = orderService.createOrder(user,shippingAddress);
        return new ResponseEntity<Order>(orders, HttpStatus.CREATED);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Order>> usersOrderHistory(@RequestHeader("Authorization") String jwt) throws UserException {
        User user = userService.findUserProfileByJwt(jwt);
        List<Order> orders = orderService.usersOrderHistory(user.getId());
        return new ResponseEntity<>(orders, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> findOrderById(@PathVariable("id") Long orderId, @RequestHeader("Authorization") String jwt) throws UserException, OrderException {
        User user = userService.findUserProfileByJwt(jwt);
        Order orders = orderService.findOrderById(orderId);
        return new ResponseEntity<>(orders, HttpStatus.ACCEPTED);
    }
}
