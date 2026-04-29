package com.NBAFanFinder.Backend.unit.Chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.NBAFanFinder.Backend.Entities.Chat;
import com.NBAFanFinder.Backend.Entities.User;
import com.NBAFanFinder.Backend.Repositories.ChatRepository;
import com.NBAFanFinder.Backend.Repositories.UserRepository;
import com.NBAFanFinder.Backend.Services.ChatService;

public class JoinChatTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatService chatService;

    private User user;
    private Chat chat1;

     @BeforeEach
    void setUp() {
        user = new User("Dupont", "Jean", "jean.dupont@email.com", "hashedPassword");
        user.setId(1L);

        chat1 = new Chat("Chat NBA", "Discussion NBA");
        chat1.setId(10L);
    }

    // @Test
    // @DisplayName("")
    // void shouldJoinChatSuccessfully() {
        
    // }
}
