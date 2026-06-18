package com.NBAFanFinder.Backend.unit.Chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
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
import com.NBAFanFinder.Backend.DTOs.Chats.UpdateChatRequest;
import com.NBAFanFinder.Backend.Entities.Chat;
import com.NBAFanFinder.Backend.Entities.User;
import com.NBAFanFinder.Backend.Exceptions.NotFoundException;
import com.NBAFanFinder.Backend.Exceptions.UnauthorizedException;
import com.NBAFanFinder.Backend.Repositories.ChatRepository;
import com.NBAFanFinder.Backend.Repositories.UserRepository;
import com.NBAFanFinder.Backend.Services.ChatService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - ChatService (createChat avancé, updateChat, removeMember)")
public class ChatServiceAdvancedTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatService chatService;

    private User owner;
    private Chat chat;

    @BeforeEach
    void setUp() {
        owner = new User("Dupont", "Jean", "jean@email.com", "pwd");
        owner.setId(1L);
        owner.setActif(true);

        chat = new Chat("Chat NBA", "Discussion NBA");
        chat.setId(10L);
    }

    // ---------- createChat : co-propriétaires & membres ----------

    @Test
    @DisplayName("createChat ajoute co-propriétaires et membres, en ignorant le créateur")
    void shouldCreateChatWithCoOwnersAndMembers() {
        User coOwner = new User("Martin", "Paul", "paul@email.com", "pwd");
        coOwner.setId(2L);
        User membre = new User("Bernard", "Luc", "luc@email.com", "pwd");
        membre.setId(3L);

        // membresIds = [3], proprietairesIds = [1, 2] (1 = créateur, doit être ignoré)
        CreateChatRequest request = new CreateChatRequest(
            "Nouveau Chat", "Une description", java.util.List.of(3L), java.util.List.of(1L, 2L)
        );
        when(userRepository.findByEmail("jean@email.com")).thenReturn(Optional.of(owner));
        when(userRepository.findById(2L)).thenReturn(Optional.of(coOwner));
        when(userRepository.findById(3L)).thenReturn(Optional.of(membre));

        chatService.createChat(request, "jean@email.com");

        assertThat(coOwner.getChatsPossedes()).hasSize(1);
        assertThat(membre.getChatsRejoints()).hasSize(1);
        verify(userRepository).save(coOwner);
        verify(userRepository).save(membre);
        // Le créateur n'est jamais re-cherché par id
        verify(userRepository, never()).findById(1L);
    }

    @Test
    @DisplayName("createChat lève IllegalArgumentException si un co-propriétaire est introuvable")
    void shouldThrowWhenCoOwnerNotFound() {
        CreateChatRequest request = new CreateChatRequest(
            "Nouveau Chat", "Une description", null, java.util.List.of(99L)
        );
        when(userRepository.findByEmail("jean@email.com")).thenReturn(Optional.of(owner));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.createChat(request, "jean@email.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("createChat lève NotFoundException si le créateur est introuvable")
    void shouldThrowWhenCreatorNotFound() {
        CreateChatRequest request = new CreateChatRequest("Nouveau Chat", "Une description", null, null);
        when(userRepository.findByEmail("inconnu@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.createChat(request, "inconnu@email.com"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Utilisateur non trouvé");
    }

    // ---------- updateChat ----------

    @Test
    @DisplayName("updateChat met à jour nom et description quand l'utilisateur est admin")
    void shouldUpdateChatWhenAdmin() {
        chat.getProprietaires().add(owner);
        UpdateChatRequest request = new UpdateChatRequest("Nouveau nom", "Nouvelle description");
        when(chatRepository.findById(10L)).thenReturn(Optional.of(chat));
        when(userRepository.findByEmail("jean@email.com")).thenReturn(Optional.of(owner));

        chatService.updateChat(10L, request, "jean@email.com");

        assertThat(chat.getNom()).isEqualTo("Nouveau nom");
        assertThat(chat.getDescription()).isEqualTo("Nouvelle description");
        verify(chatRepository).save(chat);
    }

    @Test
    @DisplayName("updateChat conserve le nom si le nouveau nom est vide")
    void shouldKeepNameWhenBlank() {
        chat.getProprietaires().add(owner);
        UpdateChatRequest request = new UpdateChatRequest("   ", null);
        when(chatRepository.findById(10L)).thenReturn(Optional.of(chat));
        when(userRepository.findByEmail("jean@email.com")).thenReturn(Optional.of(owner));

        chatService.updateChat(10L, request, "jean@email.com");

        assertThat(chat.getNom()).isEqualTo("Chat NBA");
        assertThat(chat.getDescription()).isEqualTo("Discussion NBA");
    }

    @Test
    @DisplayName("updateChat refuse un utilisateur non administrateur")
    void shouldRejectUpdateWhenNotAdmin() {
        User notAdmin = new User("Martin", "Paul", "paul@email.com", "pwd");
        notAdmin.setId(2L);
        UpdateChatRequest request = new UpdateChatRequest("Nom", "Desc");
        when(chatRepository.findById(10L)).thenReturn(Optional.of(chat));
        when(userRepository.findByEmail("paul@email.com")).thenReturn(Optional.of(notAdmin));

        assertThatThrownBy(() -> chatService.updateChat(10L, request, "paul@email.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("administrateur");
    }

    @Test
    @DisplayName("updateChat lève NotFoundException si le chat est introuvable")
    void shouldThrowWhenChatNotFoundOnUpdate() {
        UpdateChatRequest request = new UpdateChatRequest("Nom", "Desc");
        when(chatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.updateChat(99L, request, "jean@email.com"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Chat non trouvé");
    }

    // ---------- removeMember ----------

    @Test
    @DisplayName("removeMember retire un membre quand l'utilisateur est admin")
    void shouldRemoveMemberWhenAdmin() {
        chat.getProprietaires().add(owner);
        User member = new User("Bernard", "Luc", "luc@email.com", "pwd");
        member.setId(3L);
        member.getChatsRejoints().add(chat);
        when(chatRepository.findById(10L)).thenReturn(Optional.of(chat));
        when(userRepository.findByEmail("jean@email.com")).thenReturn(Optional.of(owner));
        when(userRepository.findById(3L)).thenReturn(Optional.of(member));

        chatService.removeMember(10L, 3L, "jean@email.com");

        assertThat(member.getChatsRejoints()).doesNotContain(chat);
        verify(userRepository).save(member);
    }

    @Test
    @DisplayName("removeMember refuse un utilisateur non administrateur")
    void shouldRejectRemoveWhenNotAdmin() {
        User notAdmin = new User("Martin", "Paul", "paul@email.com", "pwd");
        notAdmin.setId(2L);
        when(chatRepository.findById(10L)).thenReturn(Optional.of(chat));
        when(userRepository.findByEmail("paul@email.com")).thenReturn(Optional.of(notAdmin));

        assertThatThrownBy(() -> chatService.removeMember(10L, 3L, "paul@email.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("administrateur");
    }

    @Test
    @DisplayName("removeMember lève NotFoundException si le membre est introuvable")
    void shouldThrowWhenMemberNotFound() {
        chat.getProprietaires().add(owner);
        when(chatRepository.findById(10L)).thenReturn(Optional.of(chat));
        when(userRepository.findByEmail("jean@email.com")).thenReturn(Optional.of(owner));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.removeMember(10L, 99L, "jean@email.com"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Utilisateur non trouvé");
    }
}
