package com.quanhm.ecommerce.be.service;

import com.quanhm.ecommerce.be.model.ChatMessage;
import com.quanhm.ecommerce.be.model.User;
import com.quanhm.ecommerce.be.repository.ChatMessageRepository;
import com.quanhm.ecommerce.be.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatServiceImplementation implements ChatService {

    private ChatMessageRepository chatMessageRepository;
    private UserRepository userRepository;

    public ChatServiceImplementation(ChatMessageRepository chatMessageRepository, UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ChatMessage sendMessage(User sender, User receiver, String content) {
        ChatMessage message = new ChatMessage(sender, receiver, content);
        return chatMessageRepository.save(message);
    }

    @Override
    public ChatMessage sendMessage(User sender, User receiver, String content, String imageUrl) {
        ChatMessage message = new ChatMessage(sender, receiver, content, imageUrl);
        return chatMessageRepository.save(message);
    }

    @Override
    public List<ChatMessage> getConversation(Long userId1, Long userId2) {
        return chatMessageRepository.findConversationBetweenUsers(userId1, userId2);
    }

    @Override
    public List<ChatMessage> getUserMessages(User user) {
        return chatMessageRepository.findByReceiver(user);
    }

    @Override
    public List<ChatMessage> getUnreadMessages(User user) {
        return chatMessageRepository.findUnreadMessages(user.getId());
    }

    @Override
    public Long getUnreadMessageCount(User user) {
        return chatMessageRepository.countUnreadMessages(user.getId());
    }

    @Override
    public void markMessagesAsRead(Long senderId, Long receiverId) {
        List<ChatMessage> messages = chatMessageRepository.findConversationBetweenUsers(senderId, receiverId);
        messages.stream()
                .filter(msg -> msg.getReceiver().getId().equals(receiverId) && !msg.getIsRead())
                .forEach(msg -> msg.setIsRead(true));
        chatMessageRepository.saveAll(messages);
    }

    @Override
    public List<User> getUsersWithConversations(User currentUser) {
        Set<Long> userIds = new HashSet<>();
        
        // Get all users that current user has conversations with
        List<ChatMessage> sentMessages = chatMessageRepository.findBySender(currentUser);
        List<ChatMessage> receivedMessages = chatMessageRepository.findByReceiver(currentUser);
        
        sentMessages.forEach(msg -> userIds.add(msg.getReceiver().getId()));
        receivedMessages.forEach(msg -> userIds.add(msg.getSender().getId()));
        
        List<User> users = new ArrayList<>();
        for (Long userId : userIds) {
            userRepository.findById(userId).ifPresent(users::add);
        }
        
        return users;
    }

    @Override
    public void deleteMessage(Long messageId, User user) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin nhắn"));
        
        // Chỉ cho phép người gửi hoặc người nhận xóa tin nhắn
        if (!message.getSender().getId().equals(user.getId()) && 
            !message.getReceiver().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa tin nhắn này");
        }
        
        chatMessageRepository.delete(message);
    }

    @Override
    public void deleteConversation(Long userId1, Long userId2, User currentUser) {
        // Kiểm tra quyền: chỉ cho phép xóa cuộc trò chuyện nếu currentUser là một trong hai người
        if (!currentUser.getId().equals(userId1) && !currentUser.getId().equals(userId2)) {
            throw new RuntimeException("Bạn không có quyền xóa cuộc trò chuyện này");
        }
        
        // Lấy tất cả tin nhắn trong cuộc trò chuyện
        List<ChatMessage> messages = chatMessageRepository.findConversationBetweenUsers(userId1, userId2);
        
        // Xóa tất cả tin nhắn
        chatMessageRepository.deleteAll(messages);
    }
}
