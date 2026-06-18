package com.NBAFanFinder.Backend.integration.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.NBAFanFinder.Backend.Entities.Chat;
import com.NBAFanFinder.Backend.Entities.Message;
import com.NBAFanFinder.Backend.Entities.User;
import com.NBAFanFinder.Backend.Repositories.ChatRepository;
import com.NBAFanFinder.Backend.Repositories.MessageRepository;
import com.NBAFanFinder.Backend.Repositories.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Tests d'intégration - Repositories (requêtes JPQL personnalisées sur H2)")
public class RepositoryIntegrationTest {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    private Chat saveChat(String nom, String inviteCode) {
        Chat chat = new Chat(nom, "Description " + nom);
        chat.setInviteCode(inviteCode);
        return chatRepository.save(chat);
    }

    private User saveUser(String email) {
        User user = new User("Nom", "Prenom", email, "hashed");
        user.setActif(true);
        return userRepository.save(user);
    }

    @Test
    @DisplayName("getAllChatsForUser retourne les chats possédés ET rejoints, sans doublon")
    void shouldReturnOwnedAndJoinedChats() {
        Chat owned = saveChat("Chat possédé", "code-owned");
        Chat joined = saveChat("Chat rejoint", "code-joined");
        Chat foreign = saveChat("Chat étranger", "code-foreign");

        User user = saveUser("user@email.com");
        user.getChatsPossedes().add(owned);
        user.getChatsRejoints().add(joined);
        userRepository.save(user);

        // Un autre utilisateur possède le chat étranger
        User other = saveUser("other@email.com");
        other.getChatsPossedes().add(foreign);
        userRepository.save(other);

        List<Chat> result = chatRepository.getAllChatsForUser(user.getId());

        assertThat(result).extracting(Chat::getNom)
                .containsExactlyInAnyOrder("Chat possédé", "Chat rejoint")
                .doesNotContain("Chat étranger");
    }

    @Test
    @DisplayName("getAllChatsForUser retourne une liste vide si l'utilisateur n'a aucun chat")
    void shouldReturnEmptyWhenNoChats() {
        User user = saveUser("solo@email.com");

        assertThat(chatRepository.getAllChatsForUser(user.getId())).isEmpty();
    }

    @Test
    @DisplayName("findByChatId retourne les messages d'un chat triés par date d'envoi croissante")
    void shouldReturnMessagesOrderedByDate() {
        Chat chat = saveChat("Chat messages", "code-msg");
        User author = saveUser("author@email.com");

        // Persistés dans le désordre pour vérifier le tri de la requête
        messageRepository.save(new Message("Second", chat, author, LocalDateTime.of(2024, 1, 1, 12, 0)));
        messageRepository.save(new Message("Premier", chat, author, LocalDateTime.of(2024, 1, 1, 10, 0)));

        List<Message> result = messageRepository.findByChatId(chat.getId());

        assertThat(result).extracting(Message::getTexte).containsExactly("Premier", "Second");
    }

    @Test
    @DisplayName("findByChatId retourne une liste vide pour un chat sans message")
    void shouldReturnEmptyWhenNoMessages() {
        Chat chat = saveChat("Chat vide", "code-vide");

        assertThat(messageRepository.findByChatId(chat.getId())).isEmpty();
    }
}
