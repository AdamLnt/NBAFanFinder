package com.NBAFanFinder.Backend.unit.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.NBAFanFinder.Backend.Controllers.ChatController;
import com.NBAFanFinder.Backend.DTOs.Chats.AllChatsResponse;
import com.NBAFanFinder.Backend.DTOs.Chats.CreateChatRequest;
import com.NBAFanFinder.Backend.DTOs.Chats.JoinChatRequest;
import com.NBAFanFinder.Backend.DTOs.Chats.UpdateChatRequest;
import com.NBAFanFinder.Backend.Services.ChatService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - ChatController")
public class ChatControllerTest {

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatController chatController;

    @BeforeEach
    void setAuth() {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("jean@email.com", null));
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("GET /api/chat/ping renvoie un message de service")
    void shouldPing() {
        assertThat(chatController.ping().getBody()).contains("running");
    }

    @Test
    @DisplayName("GET /api/chat renvoie les chats de l'utilisateur authentifié")
    void shouldGetAllChats() {
        when(chatService.getAllChatsFromUser("jean@email.com"))
            .thenReturn(List.of(new AllChatsResponse(1L, "Chat", "Desc", "code", List.of(), List.of())));

        ResponseEntity<List<AllChatsResponse>> response = chatController.getAllChats();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("POST /api/chat/create renvoie 201")
    void shouldCreateChat() {
        CreateChatRequest request = new CreateChatRequest("Chat", "Desc", null, null);

        ResponseEntity<String> response = chatController.createChat(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(chatService).createChat(request, "jean@email.com");
    }

    @Test
    @DisplayName("POST /api/chat/join renvoie 200")
    void shouldJoinChat() {
        JoinChatRequest request = new JoinChatRequest(1L, "code");

        ResponseEntity<String> response = chatController.joinChat(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(chatService).joinChat(request, "jean@email.com");
    }

    @Test
    @DisplayName("PATCH /api/chat/{id} met à jour le chat")
    void shouldUpdateChat() {
        UpdateChatRequest request = new UpdateChatRequest("Nom", "Desc");

        ResponseEntity<String> response = chatController.updateChat(1L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(chatService).updateChat(1L, request, "jean@email.com");
    }

    @Test
    @DisplayName("DELETE /api/chat/{id}/members/{memberId} retire un membre")
    void shouldRemoveMember() {
        ResponseEntity<String> response = chatController.removeMember(1L, 2L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(chatService).removeMember(1L, 2L, "jean@email.com");
    }
}
