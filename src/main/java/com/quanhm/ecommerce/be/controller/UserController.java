package com.quanhm.ecommerce.be.controller;

import com.quanhm.ecommerce.be.exception.UserException;
import com.quanhm.ecommerce.be.model.User;
import com.quanhm.ecommerce.be.repository.CartItemRepository;
import com.quanhm.ecommerce.be.repository.CartRepository;
import com.quanhm.ecommerce.be.repository.UserRepository;
import com.quanhm.ecommerce.be.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

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

    @PostMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateAvatar(
            @RequestHeader("Authorization") String jwt,
            @RequestPart("avatar") MultipartFile avatarFile
    ) throws UserException, IOException {
        // Lấy thông tin user từ JWT
        User user = userService.findUserProfileByJwt(jwt);

        if (avatarFile == null || avatarFile.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "File avatar không được để trống"));
        }

        // Kiểm tra loại file (chỉ cho phép ảnh)
        String contentType = avatarFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of("message", "File phải là hình ảnh"));
        }

        // Tạo thư mục avatars nếu chưa tồn tại
        Path uploadDir = Paths.get("uploads/avatars");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Xóa avatar cũ nếu có
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            try {
                String oldFileName = user.getAvatar().replace("/uploads/avatars/", "");
                Path oldFilePath = uploadDir.resolve(oldFileName);
                if (Files.exists(oldFilePath)) {
                    Files.delete(oldFilePath);
                }
            } catch (Exception e) {
                // Bỏ qua lỗi nếu không xóa được file cũ
            }
        }

        // Tạo tên file mới với UUID để tránh trùng lặp
        String originalFileName = avatarFile.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + fileExtension;
        Path filePath = uploadDir.resolve(fileName);

        // Lưu file
        Files.copy(avatarFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Cập nhật avatar URL vào database
        String avatarUrl = "/uploads/avatars/" + fileName;
        user.setAvatar(avatarUrl);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật avatar thành công",
                "avatarUrl", avatarUrl
        ));
    }
}
