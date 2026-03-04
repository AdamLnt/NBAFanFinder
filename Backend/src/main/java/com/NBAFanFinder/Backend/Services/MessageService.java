package com.NBAFanFinder.Backend.Services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.NBAFanFinder.Backend.DTOs.Messages.AllMessagesResponse;
import com.NBAFanFinder.Backend.DTOs.Messages.SendMessageRequest;
import com.NBAFanFinder.Backend.Entities.Chat;
import com.NBAFanFinder.Backend.Entities.Message;
import com.NBAFanFinder.Backend.Entities.User;
import com.NBAFanFinder.Backend.Exceptions.NotFoundException;
import com.NBAFanFinder.Backend.Repositories.ChatRepository;
import com.NBAFanFinder.Backend.Repositories.MessageRepository;
import com.NBAFanFinder.Backend.Repositories.UserRepository;

@Service
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, ChatRepository chatRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    public List<AllMessagesResponse> getAllMessagesFromChat(Long chatId) {
        return messageRepository.findByChatId(chatId).stream()
                .map(AllMessagesResponse::from)
                .toList();
    }

    public void sendMessage(SendMessageRequest request) {
        Chat chat = chatRepository.findById(request.chatId())
                .orElseThrow(() -> new NotFoundException("Chat non trouvé"));
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        messageRepository.save(new Message(request.texte(), chat, user));
    }
}
