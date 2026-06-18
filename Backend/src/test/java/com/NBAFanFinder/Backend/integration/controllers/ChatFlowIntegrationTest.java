package com.NBAFanFinder.Backend.integration.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.NBAFanFinder.Backend.DTOs.Chats.CreateChatRequest;
import com.NBAFanFinder.Backend.DTOs.Chats.JoinChatRequest;
import com.NBAFanFinder.Backend.DTOs.Messages.SendMessageRequest;
import com.NBAFanFinder.Backend.Entities.Chat;
import com.NBAFanFinder.Backend.Entities.User;
import com.NBAFanFinder.Backend.Repositories.ChatRepository;
import com.NBAFanFinder.Backend.Repositories.UserRepository;
import com.NBAFanFinder.Backend.Security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Tests d'intégration - parcours chat authentifié (filtre JWT -> contrôleur -> service -> H2)")
public class ChatFlowIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // La chaîne de filtres de sécurité (inclut le JwtFilter) : il faut l'appliquer
    // explicitement, sinon webAppContextSetup ne passe pas par Spring Security.
    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @PersistenceContext
    private EntityManager entityManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;
    private String ownerToken;

    @PostConstruct
    void initMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    @BeforeEach
    void setUp() {
        User owner = new User("Owner", "Alice", "owner.alice@email.com", passwordEncoder.encode("Secret1!@xx"));
        owner.setActif(true);
        userRepository.save(owner);
        ownerToken = jwtUtil.generateToken(owner.getEmail());
    }

    private String bearer() {
        return "Bearer " + ownerToken;
    }

    // Vide le contexte de persistance partagé du test : chaque requête MockMvc suivante
    // recharge alors les entités depuis H2, comme le ferait une vraie requête HTTP isolée.
    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Refuse l'accès aux chats sans authentification")
    void shouldRejectUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/chat"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Crée un chat, le liste, envoie et relit un message de bout en bout")
    void shouldRunFullChatFlow() throws Exception {
        // 1. Création du chat (POST /api/chat/create)
        CreateChatRequest createRequest = new CreateChatRequest("Fans Lakers", "Discussion Lakers", null, null);
        mockMvc.perform(post("/api/chat/create")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());
        flushAndClear();

        // 2. Le chat apparaît dans la liste de l'utilisateur (GET /api/chat)
        mockMvc.perform(get("/api/chat").header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Fans Lakers"));

        Chat chat = chatRepository.findAll().stream()
                .filter(c -> "Fans Lakers".equals(c.getNom()))
                .findFirst()
                .orElseThrow();

        // 3. Envoi d'un message (POST /api/messages/send)
        SendMessageRequest sendRequest = new SendMessageRequest(chat.getId(), "Bonjour la team !");
        mockMvc.perform(post("/api/messages/send")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendRequest)))
                .andExpect(status().isOk());

        // 4. Relecture des messages (GET /api/messages/chat/{id})
        mockMvc.perform(get("/api/messages/chat/" + chat.getId()).header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].texte").value("Bonjour la team !"))
                .andExpect(jsonPath("$[0].nom_utilisateur").value("Owner"));
    }

    @Test
    @DisplayName("Un second utilisateur rejoint le chat via le code d'invitation")
    void shouldAllowAnotherUserToJoin() throws Exception {
        // Owner crée un chat
        CreateChatRequest createRequest = new CreateChatRequest("Chat ouvert", "Tout le monde", null, null);
        mockMvc.perform(post("/api/chat/create")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());
        flushAndClear();

        Chat chat = chatRepository.findAll().stream()
                .filter(c -> "Chat ouvert".equals(c.getNom()))
                .findFirst()
                .orElseThrow();

        // Un second utilisateur s'authentifie et rejoint
        User member = new User("Member", "Bob", "member.bob@email.com", passwordEncoder.encode("Secret1!@xx"));
        member.setActif(true);
        userRepository.save(member);
        String memberToken = jwtUtil.generateToken(member.getEmail());

        JoinChatRequest joinRequest = new JoinChatRequest(chat.getId(), chat.getInviteCode());
        mockMvc.perform(post("/api/chat/join")
                        .header("Authorization", "Bearer " + memberToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isOk());

        // Le membre voit désormais le chat dans sa liste
        mockMvc.perform(get("/api/chat").header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Chat ouvert"));
    }
}
