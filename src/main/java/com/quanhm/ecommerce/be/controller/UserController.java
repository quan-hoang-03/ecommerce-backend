package com.quanhm.ecommerce.be.controller;

import com.quanhm.ecommerce.be.model.User;
import com.quanhm.ecommerce.be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        return ResponseEntity.ok(user);
    }
}
