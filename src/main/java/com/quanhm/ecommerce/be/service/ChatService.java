package com.quanhm.ecommerce.be.service;

import com.quanhm.ecommerce.be.model.ChatMessage;
import com.quanhm.ecommerce.be.model.User;

import java.util.List;

public interface ChatService {
    ChatMessage sendMessage(User sender, User receiver, String content);
    
    ChatMessage sendMessage(User sender, User receiver, String content, String imageUrl);
    
    List<ChatMessage> getConversation(Long userId1, Long userId2);
    
    List<ChatMessage> getUserMessages(User user);
    
    List<ChatMessage> getUnreadMessages(User user);
    
    Long getUnreadMessageCount(User user);
    
    void markMessagesAsRead(Long senderId, Long receiverId);
    
    List<User> getUsersWithConversations(User currentUser);
    
    void deleteMessage(Long messageId, User user);
    
    void deleteConversation(Long userId1, Long userId2, User currentUser);
}
