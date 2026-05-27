package com.NBAFanFinder.Backend.unit.Chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import com.NBAFanFinder.Backend.DTOs.Chats.JoinChatRequest;
import com.NBAFanFinder.Backend.Entities.Chat;
import com.NBAFanFinder.Backend.Entities.User;
import com.NBAFanFinder.Backend.Exceptions.NotFoundException;
import com.NBAFanFinder.Backend.Exceptions.UnauthorizedException;
import com.NBAFanFinder.Backend.Repositories.ChatRepository;
import com.NBAFanFinder.Backend.Repositories.UserRepository;
import com.NBAFanFinder.Backend.Services.ChatService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - ChatService#joinChat")
public class JoinChatTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatService chatService;

    private static final String VALID_CODE = "invite-code-abc";

    private User user;
    private Chat chat;

    @BeforeEach
    void setUp() {
        user = new User("Dupont", "Jean", "jean.dupont@email.com", "hashedPassword");
        user.setId(1L);

        chat = new Chat("Chat NBA", "Discussion NBA");
        chat.setId(10L);
        chat.setInviteCode(VALID_CODE);
    }

    @Test
    @DisplayName("Rejoint un chat existant avec succès quand le code d'invitation est valide")
    void shouldJoinChatSuccessfully() {
        JoinChatRequest request = new JoinChatRequest(10L, VALID_CODE);
        when(chatRepository.findById(10L)).thenReturn(Optional.of(chat));
        when(userRepository.findByEmail("jean.dupont@email.com")).thenReturn(Optional.of(user));

        chatService.joinChat(request, "jean.dupont@email.com");

        assertThat(user.getChatsRejoints()).contains(chat);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Lève UnauthorizedException si le code d'invitation est absent")
    void shouldThrowWhenInviteCodeMissing() {
        JoinChatRequest request = new JoinChatRequest(10L, null);

        assertThatThrownBy(() -> chatService.joinChat(request, "jean.dupont@email.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Code d'invitation requis");
    }

    @Test
    @DisplayName("Lève UnauthorizedException si le code d'invitation est invalide")
    void shouldThrowWhenInviteCodeWrong() {
        JoinChatRequest request = new JoinChatRequest(10L, "wrong-code");
        when(chatRepository.findById(10L)).thenReturn(Optional.of(chat));

        assertThatThrownBy(() -> chatService.joinChat(request, "jean.dupont@email.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Code d'invitation invalide");
    }

    @Test
    @DisplayName("Lève NotFoundException si le chat n'existe pas")
    void shouldThrowWhenChatNotFound() {
        JoinChatRequest request = new JoinChatRequest(999L, VALID_CODE);
        when(chatRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.joinChat(request, "jean.dupont@email.com"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Chat non trouvé");
    }

    @Test
    @DisplayName("Lève NotFoundException si l'utilisateur n'existe pas")
    void shouldThrowWhenUserNotFound() {
        JoinChatRequest request = new JoinChatRequest(10L, VALID_CODE);
        when(chatRepository.findById(10L)).thenReturn(Optional.of(chat));
        when(userRepository.findByEmail("inconnu@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.joinChat(request, "inconnu@email.com"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Utilisateur non trouvé");
    }
}
