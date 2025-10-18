package com.quanhm.ecommerce.be.controller;

import com.quanhm.ecommerce.be.exception.UserException;
import com.quanhm.ecommerce.be.model.User;
import com.quanhm.ecommerce.be.repository.CartItemRepository;
import com.quanhm.ecommerce.be.repository.CartRepository;
import com.quanhm.ecommerce.be.repository.UserRepository;
import com.quanhm.ecommerce.be.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String jwt) throws UserException {
        User user = userService.findUserProfileByJwt(jwt);
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }
    @PostMapping("/update-role/{userId}")
    public ResponseEntity<?> updateUserRole(@PathVariable Long userId, @RequestParam String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        user.setRole(role.toUpperCase());
        userRepository.save(user);
        return ResponseEntity.ok("Đã cập nhật quyền cho " + user.getEmail() + " thành " + role);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        // Kiểm tra người dùng tồn tại
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("Không thể xóa tài khoản ADMIN!");
        }
        cartItemRepository.deleteByUserItemCartId(userId);

        cartRepository.deleteByUserId(userId);
        // Thực hiện xóa
        userRepository.delete(user);

        return ResponseEntity.ok("Đã xóa người dùng: " + user.getEmail());
    }
}
