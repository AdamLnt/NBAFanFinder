package com.NBAFanFinder.Backend.unit.Chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.NBAFanFinder.Backend.DTOs.Chats.AllChatsResponse;
import com.NBAFanFinder.Backend.Entities.Chat;
import com.NBAFanFinder.Backend.Entities.User;
import com.NBAFanFinder.Backend.Exceptions.NotFoundException;
import com.NBAFanFinder.Backend.Repositories.ChatRepository;
import com.NBAFanFinder.Backend.Repositories.UserRepository;
import com.NBAFanFinder.Backend.Services.ChatService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - ChatService#getAllChatsFromUser")
public class GetAllChatsTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatService chatService;

    private User user;
    private Chat chat1;
    private Chat chat2;

    @BeforeEach
    void setUp() {
        user = new User("Dupont", "Jean", "jean.dupont@email.com", "hashedPassword");
        user.setId(1L);

        chat1 = new Chat("Chat NBA", "Discussion NBA");
        chat1.setId(10L);

        chat2 = new Chat("Chat Lakers", "Fans des Lakers");
        chat2.setId(11L);
    }

    @Test
    @DisplayName("Retourne la liste des chats de l'utilisateur")
    void shouldGetAllChatsFromUserSuccessfully() {
        when(userRepository.findByEmail("jean.dupont@email.com")).thenReturn(Optional.of(user));
        when(chatRepository.getAllChatsForUser(1L)).thenReturn(List.of(chat1, chat2));

        List<AllChatsResponse> result = chatService.getAllChatsFromUser("jean.dupont@email.com");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(10L);
        assertThat(result.get(0).nom()).isEqualTo("Chat NBA");
        assertThat(result.get(1).id()).isEqualTo(11L);
        assertThat(result.get(1).nom()).isEqualTo("Chat Lakers");
    }

    @Test
    @DisplayName("Retourne une liste vide si l'utilisateur n'a aucun chat")
    void shouldReturnEmptyListWhenUserHasNoChats() {
        when(userRepository.findByEmail("jean.dupont@email.com")).thenReturn(Optional.of(user));
        when(chatRepository.getAllChatsForUser(1L)).thenReturn(List.of());

        List<AllChatsResponse> result = chatService.getAllChatsFromUser("jean.dupont@email.com");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Lève une NotFoundException si l'utilisateur est introuvable")
    void shouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findByEmail("inconnu@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.getAllChatsFromUser("inconnu@email.com"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Utilisateur non trouvé");
    }
}
