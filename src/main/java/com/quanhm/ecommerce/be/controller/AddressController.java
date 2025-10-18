package com.quanhm.ecommerce.be.controller;

import com.quanhm.ecommerce.be.exception.UserException;
import com.quanhm.ecommerce.be.model.Address;
import com.quanhm.ecommerce.be.model.Order;
import com.quanhm.ecommerce.be.model.User;
import com.quanhm.ecommerce.be.repository.AddressRepository;
import com.quanhm.ecommerce.be.repository.OrderRepository;
import com.quanhm.ecommerce.be.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressController {
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping
    public ResponseEntity<List<Address>> getAddresses(@RequestHeader("Authorization") String jwt) throws UserException {
        User user = userService.findUserProfileByJwt(jwt);
        List<Address> addresses = addressRepository.findByUser(user);
        return ResponseEntity.ok(addresses);
    }
    @PostMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt
    ) throws UserException {
        User user = userService.findUserProfileByJwt(jwt);

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // ✅ Chỉ cho xóa nếu là địa chỉ của user hiện tại
        if (!address.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        // ✅ Tìm các đơn hàng đang dùng địa chỉ này
        List<Order> orders = orderRepository.findByShippingAddress(address);
        for (Order order : orders) {
            order.setShippingAddress(null); // ngắt liên kết
        }
        orderRepository.saveAll(orders);

        // ✅ Xóa địa chỉ
        addressRepository.delete(address);

        return ResponseEntity.noContent().build();
    }
}
