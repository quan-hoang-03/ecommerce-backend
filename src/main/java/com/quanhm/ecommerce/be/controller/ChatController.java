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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
