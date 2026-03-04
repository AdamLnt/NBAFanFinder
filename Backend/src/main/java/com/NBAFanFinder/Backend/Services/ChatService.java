package com.NBAFanFinder.Backend.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.NBAFanFinder.Backend.DTOs.Chats.AllChatsResponse;
import com.NBAFanFinder.Backend.DTOs.Chats.CreateChatRequest;
import com.NBAFanFinder.Backend.DTOs.Chats.JoinChatRequest;
import com.NBAFanFinder.Backend.Entities.Chat;
import com.NBAFanFinder.Backend.Entities.User;
import com.NBAFanFinder.Backend.Repositories.ChatRepository;
import com.NBAFanFinder.Backend.Repositories.UserRepository;

@Service
@Transactional
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public ChatService(ChatRepository chatRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    public List<AllChatsResponse> getAllChatsFromUser(long id) {
        return chatRepository.getAllChatsForUser(id).stream()
                .map(AllChatsResponse::from)
                .toList();
    }

    public void createChat(CreateChatRequest request) {
        if (request.nom() == null || request.nom().isEmpty() 
            || request.description() == null || request.description().isEmpty()) {
            throw new IllegalArgumentException("L'un des champs est incorrect");
        }

        User proprietaire = userRepository.findById(request.userId())
        .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        Chat chat = new Chat(request.nom(), request.description());
        chatRepository.save(chat);

        proprietaire.getChatsPossedes().add(chat);
        userRepository.save(proprietaire);

    }

    public void joinChat(JoinChatRequest request) {
        Chat chat = chatRepository.findById(request.chatId())
            .orElseThrow(() -> new IllegalArgumentException("Chat non trouvé"));

        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        user.getChatsRejoints().add(chat);
        userRepository.save(user);
    }
}
