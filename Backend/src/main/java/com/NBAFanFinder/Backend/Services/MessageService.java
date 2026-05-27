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
import com.NBAFanFinder.Backend.Exceptions.UnauthorizedException;
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

    public List<AllMessagesResponse> getAllMessagesFromChat(Long chatId, String email) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundException("Chat non trouvé"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        if (!isMember(chat, user)) {
            throw new UnauthorizedException("Accès interdit : vous n'appartenez pas à ce chat");
        }

        return messageRepository.findByChatId(chatId).stream()
                .map(AllMessagesResponse::from)
                .toList();
    }

    public void sendMessage(SendMessageRequest request, String email) {
        Chat chat = chatRepository.findById(request.chatId())
                .orElseThrow(() -> new NotFoundException("Chat non trouvé"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        if (!isMember(chat, user)) {
            throw new UnauthorizedException("Accès interdit : vous n'appartenez pas à ce chat");
        }

        messageRepository.save(new Message(request.texte(), chat, user));
    }

    private boolean isMember(Chat chat, User user) {
        Long uid = user.getId();
        boolean isOwner = chat.getProprietaires().stream().anyMatch(p -> p.getId().equals(uid));
        boolean isJoined = chat.getMembres().stream().anyMatch(m -> m.getId().equals(uid));
        return isOwner || isJoined;
    }
}
