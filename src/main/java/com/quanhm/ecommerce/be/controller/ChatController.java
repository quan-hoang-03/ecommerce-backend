package com.quanhm.ecommerce.be.controller;

import com.quanhm.ecommerce.be.exception.UserException;
import com.quanhm.ecommerce.be.model.ChatMessage;
import com.quanhm.ecommerce.be.model.User;
import com.quanhm.ecommerce.be.repository.UserRepository;
import com.quanhm.ecommerce.be.service.ChatService;
import com.quanhm.ecommerce.be.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @RequestHeader("Authorization") String jwt,
            @RequestBody Map<String, Object> request
    ) throws UserException {
        User sender = userService.findUserProfileByJwt(jwt);
        Long receiverId = Long.parseLong(request.get("receiverId").toString());
        String content = request.get("content").toString();

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người nhận"));

        ChatMessage message = chatService.sendMessage(sender, receiver, content);

        // Gửi message qua WebSocket
        Map<String, Object> response = new HashMap<>();
        response.put("id", message.getId());
        response.put("senderId", message.getSender().getId());
        response.put("senderName", message.getSender().getFirstName() + " " + message.getSender().getLastName());
        response.put("receiverId", message.getReceiver().getId());
        response.put("receiverName", message.getReceiver().getFirstName() + " " + message.getReceiver().getLastName());
        response.put("content", message.getContent());
        response.put("imageUrl", message.getImageUrl());
        response.put("timestamp", message.getTimestamp());
        response.put("isRead", message.getIsRead());

        // Gửi tới cả sender và receiver
        messagingTemplate.convertAndSend("/topic/messages/" + sender.getId(), response);
        messagingTemplate.convertAndSend("/topic/messages/" + receiver.getId(), response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/conversation/{userId}")
    public ResponseEntity<?> getConversation(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long userId
    ) throws UserException {
        User currentUser = userService.findUserProfileByJwt(jwt);
        List<ChatMessage> messages = chatService.getConversation(currentUser.getId(), userId);

        // Mark messages as read
        chatService.markMessagesAsRead(userId, currentUser.getId());

        // Normalize messages to include senderId and receiverId for consistency
        List<Map<String, Object>> normalizedMessages = messages.stream().map(msg -> {
            Map<String, Object> normalized = new HashMap<>();
            normalized.put("id", msg.getId());
            normalized.put("senderId", msg.getSender().getId());
            normalized.put("senderName", msg.getSender().getFirstName() + " " + msg.getSender().getLastName());
            normalized.put("receiverId", msg.getReceiver().getId());
            normalized.put("receiverName", msg.getReceiver().getFirstName() + " " + msg.getReceiver().getLastName());
            normalized.put("content", msg.getContent());
            normalized.put("imageUrl", msg.getImageUrl());
            normalized.put("timestamp", msg.getTimestamp());
            normalized.put("isRead", msg.getIsRead());
            return normalized;
        }).toList();

        return ResponseEntity.ok(normalizedMessages);
    }

    @GetMapping("/messages")
    public ResponseEntity<?> getUserMessages(@RequestHeader("Authorization") String jwt) throws UserException {
        User user = userService.findUserProfileByJwt(jwt);
        List<ChatMessage> messages = chatService.getUserMessages(user);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(@RequestHeader("Authorization") String jwt) throws UserException {
        User user = userService.findUserProfileByJwt(jwt);
        Long count = chatService.getUnreadMessageCount(user);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/conversations")
    public ResponseEntity<?> getConversations(@RequestHeader("Authorization") String jwt) throws UserException {
        User currentUser = userService.findUserProfileByJwt(jwt);
        List<User> users = chatService.getUsersWithConversations(currentUser);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/mark-read/{userId}")
    public ResponseEntity<?> markAsRead(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long userId
    ) throws UserException {
        User currentUser = userService.findUserProfileByJwt(jwt);
        chatService.markMessagesAsRead(userId, currentUser.getId());
        return ResponseEntity.ok(Map.of("message", "Đã đánh dấu đã đọc"));
    }

    @GetMapping("/admin/customers")
    public ResponseEntity<?> getAllCustomers(@RequestHeader("Authorization") String jwt) throws UserException {
        User admin = userService.findUserProfileByJwt(jwt);
        
        if (!"ADMIN".equalsIgnoreCase(admin.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Không có quyền truy cập"));
        }

        List<User> customers = userRepository.findAll().stream()
                .filter(user -> !"ADMIN".equalsIgnoreCase(user.getRole()))
                .toList();

        return ResponseEntity.ok(customers);
    }

    @DeleteMapping("/message/{messageId}")
    public ResponseEntity<?> deleteMessage(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long messageId
    ) throws UserException {
        User currentUser = userService.findUserProfileByJwt(jwt);
        try {
            chatService.deleteMessage(messageId, currentUser);
            
            // Thông báo qua WebSocket để cập nhật UI
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "message_deleted");
            notification.put("messageId", messageId);
            
            return ResponseEntity.ok(Map.of("message", "Đã xóa tin nhắn thành công", "messageId", messageId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping(value = "/send-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> sendMessageWithImage(
            @RequestHeader("Authorization") String jwt,
            @RequestPart("receiverId") String receiverIdStr,
            @RequestPart(value = "content", required = false) String content,
            @RequestPart("image") MultipartFile imageFile
    ) throws UserException, IOException {
        User sender = userService.findUserProfileByJwt(jwt);
        Long receiverId = Long.parseLong(receiverIdStr);
        String messageContent = content != null ? content : "";

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người nhận"));

        // Validate image file
        if (imageFile == null || imageFile.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "File ảnh không được để trống"));
        }

        String contentType = imageFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of("message", "File phải là hình ảnh"));
        }

        // Create upload directory if not exists
        Path uploadDir = Paths.get("uploads/chat-images");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Generate unique filename
        String originalFileName = imageFile.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + fileExtension;
        Path filePath = uploadDir.resolve(fileName);

        // Save file
        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Create image URL
        String imageUrl = "/uploads/chat-images/" + fileName;

        // Save message with image
        ChatMessage message = chatService.sendMessage(sender, receiver, messageContent, imageUrl);

        // Send message via WebSocket
        Map<String, Object> response = new HashMap<>();
        response.put("id", message.getId());
        response.put("senderId", message.getSender().getId());
        response.put("senderName", message.getSender().getFirstName() + " " + message.getSender().getLastName());
        response.put("receiverId", message.getReceiver().getId());
        response.put("receiverName", message.getReceiver().getFirstName() + " " + message.getReceiver().getLastName());
        response.put("content", message.getContent());
        response.put("imageUrl", message.getImageUrl());
        response.put("timestamp", message.getTimestamp());
        response.put("isRead", message.getIsRead());

        // Send to both sender and receiver
        messagingTemplate.convertAndSend("/topic/messages/" + sender.getId(), response);
        messagingTemplate.convertAndSend("/topic/messages/" + receiver.getId(), response);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/conversation/{userId}")
    public ResponseEntity<?> deleteConversation(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long userId
    ) throws UserException {
        User currentUser = userService.findUserProfileByJwt(jwt);
        try {
            chatService.deleteConversation(currentUser.getId(), userId, currentUser);
            
            // Thông báo qua WebSocket để cập nhật UI
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "conversation_deleted");
            notification.put("userId", userId);
            
            messagingTemplate.convertAndSend("/topic/messages/" + currentUser.getId(), notification);
            messagingTemplate.convertAndSend("/topic/messages/" + userId, notification);
            
            return ResponseEntity.ok(Map.of("message", "Đã xóa toàn bộ cuộc trò chuyện thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }
}
