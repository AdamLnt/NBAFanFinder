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

import com.NBAFanFinder.Backend.Controllers.MessageController;
import com.NBAFanFinder.Backend.DTOs.Messages.AllMessagesResponse;
import com.NBAFanFinder.Backend.DTOs.Messages.SendMessageRequest;
import com.NBAFanFinder.Backend.Services.MessageService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - MessageController")
public class MessageControllerTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageController messageController;

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
    @DisplayName("GET /api/messages/ping renvoie un message de service")
    void shouldPing() {
        assertThat(messageController.ping().getBody()).contains("running");
    }

    @Test
    @DisplayName("GET /api/messages/chat/{chatId} renvoie les messages")
    void shouldGetMessages() {
        when(messageService.getAllMessagesFromChat(10L, "jean@email.com"))
            .thenReturn(List.of(new AllMessagesResponse(1L, 10L, 1L, "Salut", null, "Dupont", "Jean")));

        ResponseEntity<List<AllMessagesResponse>> response = messageController.getMessagesFromChat(10L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("POST /api/messages/send enregistre un message")
    void shouldSendMessage() {
        SendMessageRequest request = new SendMessageRequest(10L, "Hello");

        ResponseEntity<String> response = messageController.sendMessage(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(messageService).sendMessage(request, "jean@email.com");
    }
}
