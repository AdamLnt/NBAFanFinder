package com.NBAFanFinder.Backend.unit.Chat;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.NBAFanFinder.Backend.DTOs.Chats.CreateChatRequest;
import com.NBAFanFinder.Backend.Entities.User;
import com.NBAFanFinder.Backend.Repositories.ChatRepository;
import com.NBAFanFinder.Backend.Repositories.UserRepository;
import com.NBAFanFinder.Backend.Services.ChatService;

@ExtendWith(MockitoExtension.class)
public class CreateChatTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatService chatService;

    private User proprietaire;

    @BeforeEach
    void setUp() {
        proprietaire = new User("Dupont", "Jean", "jean.dupont@email.com", "hashedPassword");
        proprietaire.setId(1L);
        proprietaire.setActif(true);
    }

    @Test
    @DisplayName("Successful chat creation")
    void shouldCreateChatSuccessfully() {
        // GIVEN
        CreateChatRequest request = new CreateChatRequest("Nom de chat test", "C'est un test de chat", null, null);
        String email = "jean.dupont@email.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(proprietaire));

        // WHEN
        chatService.createChat(request, email);

        // THEN
        verify(chatRepository).save(argThat(chat ->
            chat.getNom().equals("Nom de chat test") &&
            chat.getDescription().equals("C'est un test de chat")
        ));
        verify(userRepository).save(proprietaire);
    }

    @Test
    @DisplayName("Missing/incorrect params chat creation")
    void shouldFailCreateChat() {
        //GIVEN
        CreateChatRequest request = new CreateChatRequest(null, null, null, null);
        String email = "jean.dupont@email.com";

        //WHEN
        assertThatThrownBy(() -> chatService.createChat(request, email))
        //THEN
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("L'un des champs est incorrect");
    }
}
