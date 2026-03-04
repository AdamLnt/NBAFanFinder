package com.NBAFanFinder.Backend.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.NBAFanFinder.Backend.DTOs.Chats.AllChatsResponse;
import com.NBAFanFinder.Backend.DTOs.Chats.CreateChatRequest;
import com.NBAFanFinder.Backend.DTOs.Chats.JoinChatRequest;
import com.NBAFanFinder.Backend.DTOs.Chats.UpdateChatRequest;
import com.NBAFanFinder.Backend.Entities.Chat;
import com.NBAFanFinder.Backend.Entities.User;
import com.NBAFanFinder.Backend.Exceptions.NotFoundException;
import com.NBAFanFinder.Backend.Exceptions.UnauthorizedException;
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

        if (request.proprietairesIds() != null) {
            for (Long propId : request.proprietairesIds()) {
                if (propId == request.userId()) continue;
                User coProprietaire = userRepository.findById(propId)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé: " + propId));
                coProprietaire.getChatsPossedes().add(chat);
                userRepository.save(coProprietaire);
            }
        }

        if (request.membresIds() != null) {
            for (Long membreId : request.membresIds()) {
                if (membreId == request.userId()) continue;
                User membre = userRepository.findById(membreId)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé: " + membreId));
                membre.getChatsRejoints().add(chat);
                userRepository.save(membre);
            }
        }

    }

    public void joinChat(JoinChatRequest request) {
        Chat chat = chatRepository.findById(request.chatId())
            .orElseThrow(() -> new IllegalArgumentException("Chat non trouvé"));

        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        user.getChatsRejoints().add(chat);
        userRepository.save(user);
    }

    public void updateChat(long chatId, UpdateChatRequest request) {
        Chat chat = chatRepository.findById(chatId)
            .orElseThrow(() -> new NotFoundException("Chat non trouvé"));

        boolean isAdmin = chat.getProprietaires().stream()
            .anyMatch(p -> p.getId() == request.requestUserId());
        if (!isAdmin) {
            throw new UnauthorizedException("Seul un administrateur peut modifier ce chat");
        }

        if (request.nom() != null && !request.nom().isBlank()) {
            chat.setNom(request.nom());
        }
        if (request.description() != null) {
            chat.setDescription(request.description());
        }

        chatRepository.save(chat);
    }

    public void removeMember(long chatId, long memberId, long requestUserId) {
        Chat chat = chatRepository.findById(chatId)
            .orElseThrow(() -> new NotFoundException("Chat non trouvé"));

        boolean isAdmin = chat.getProprietaires().stream()
            .anyMatch(p -> p.getId() == requestUserId);
        if (!isAdmin) {
            throw new UnauthorizedException("Seul un administrateur peut retirer des membres");
        }

        User member = userRepository.findById(memberId)
            .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        member.getChatsRejoints().remove(chat);
        userRepository.save(member);
    }
}
