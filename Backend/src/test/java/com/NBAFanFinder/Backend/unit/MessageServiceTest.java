package com.NBAFanFinder.Backend.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
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
import com.NBAFanFinder.Backend.Services.MessageService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - MessageService")
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MessageService messageService;

    private User member;
    private User stranger;
    private Chat chat;

    @BeforeEach
    void setUp() {
        member = new User("Dupont", "Jean", "jean@email.com", "pwd");
        member.setId(1L);

        stranger = new User("Martin", "Paul", "paul@email.com", "pwd");
        stranger.setId(2L);

        chat = new Chat("Chat NBA", "Discussion NBA");
        chat.setId(10L);
        chat.getMembres().add(member);
    }

    @Test
    @DisplayName("getAllMessagesFromChat retourne les messages pour un membre")
    void shouldGetAllMessages() {
        Message message = new Message("Salut !", chat, member);
        message.setId(100L);
        when(chatRepository.findById(10L)).thenReturn(Optional.of(chat));
        when(userRepository.findByEmail("jean@email.com")).thenReturn(Optional.of(member));
        when(messageRepository.findByChatId(10L)).thenReturn(List.of(message));

        List<AllMessagesResponse> result = messageService.getAllMessagesFromChat(10L, "jean@email.com");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).texte()).isEqualTo("Salut !");
        assertThat(result.get(0).nomUtilisateur()).isEqualTo("Dupont");
    }

    @Test
    @DisplayName("getAllMessagesFromChat autorise un propriétaire du chat")
    void shouldAllowOwnerToReadMessages() {
        chat.getMembres().clear();
        chat.getProprietaires().add(member);
        when(chatRepository.findById(10L)).thenReturn(Optional.of(chat));
        when(userRepository.findByEmail("jean@email.com")).thenReturn(Optional.of(member));
        when(messageRepository.findByChatId(10L)).thenReturn(List.of());

        List<AllMessagesResponse> result = messageService.getAllMessagesFromChat(10L, "jean@email.com");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getAllMessagesFromChat refuse un non-membre")
    void shouldRejectNonMemberFromReading() {
        when(chatRepository.findById(10L)).thenReturn(Optional.of(chat));
        when(userRepository.findByEmail("paul@email.com")).thenReturn(Optional.of(stranger));

        assertThatThrownBy(() -> messageService.getAllMessagesFromChat(10L, "paul@email.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("vous n'appartenez pas");
    }

    @Test
    @DisplayName("getAllMessagesFromChat lève NotFoundException si le chat est introuvable")
    void shouldThrowWhenChatNotFoundOnRead() {
        when(chatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.getAllMessagesFromChat(99L, "jean@email.com"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Chat non trouvé");
    }

    @Test
    @DisplayName("getAllMessagesFromChat lève NotFoundException si l'utilisateur est introuvable")
    void shouldThrowWhenUserNotFoundOnRead() {
        when(chatRepository.findById(10L)).thenReturn(Optional.of(chat));
        when(userRepository.findByEmail("inconnu@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.getAllMessagesFromChat(10L, "inconnu@email.com"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Utilisateur non trouvé");
    }

    @Test
    @DisplayName("sendMessage enregistre un message pour un membre")
    void shouldSendMessage() {
        SendMessageRequest request = new SendMessageRequest(10L, "Hello tout le monde");
        when(chatRepository.findById(10L)).thenReturn(Optional.of(chat));
        when(userRepository.findByEmail("jean@email.com")).thenReturn(Optional.of(member));

        messageService.sendMessage(request, "jean@email.com");

        verify(messageRepository).save(argThat(msg ->
            msg.getTexte().equals("Hello tout le monde")
            && msg.getChat() == chat
            && msg.getUtilisateur() == member
        ));
    }

    @Test
    @DisplayName("sendMessage refuse un non-membre")
    void shouldRejectNonMemberFromSending() {
        SendMessageRequest request = new SendMessageRequest(10L, "Hello");
        when(chatRepository.findById(10L)).thenReturn(Optional.of(chat));
        when(userRepository.findByEmail("paul@email.com")).thenReturn(Optional.of(stranger));

        assertThatThrownBy(() -> messageService.sendMessage(request, "paul@email.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("vous n'appartenez pas");
    }

    @Test
    @DisplayName("sendMessage lève NotFoundException si le chat est introuvable")
    void shouldThrowWhenChatNotFoundOnSend() {
        SendMessageRequest request = new SendMessageRequest(99L, "Hello");
        when(chatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.sendMessage(request, "jean@email.com"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Chat non trouvé");
    }
}
