package com.NBAFanFinder.Backend.integration.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.annotation.PostConstruct;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.NBAFanFinder.Backend.DTOs.AddressRequest;
import com.NBAFanFinder.Backend.DTOs.LoginRequest;
import com.NBAFanFinder.Backend.DTOs.RegisterRequest;
import com.NBAFanFinder.Backend.Entities.User;
import com.NBAFanFinder.Backend.Repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Tests d'intégration - AuthController")
public class AuthControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @PostConstruct
    void initMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("POST /api/auth/register crée un utilisateur et renvoie 201")
    void shouldRegisterUserViaController() throws Exception {
        AddressRequest addr = new AddressRequest(
            "10", "Rue de Rivoli", "Paris", "75001", "France", 48.8566, 2.3522
        );
        RegisterRequest req = new RegisterRequest(
            "Test", "User", "test.user@email.com", "Secure1!@Pass", null, addr, null
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test.user@email.com"))
                .andExpect(jsonPath("$.nom").value("Test"));
    }

    @Test
    @DisplayName("POST /api/auth/login retourne un token JWT pour des credentials valides")
    void shouldLoginWithValidCredentials() throws Exception {
        User user = new User("Login", "Tester", "login.tester@email.com",
                passwordEncoder.encode("Secret1!"));
        user.setActif(true);
        userRepository.save(user);

        LoginRequest req = new LoginRequest("login.tester@email.com", "Secret1!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("login.tester@email.com"));
    }

    @Test
    @DisplayName("POST /api/auth/login renvoie 401 pour un mauvais mot de passe")
    void shouldRejectLoginWithWrongPassword() throws Exception {
        User user = new User("Bad", "Pwd", "bad.pwd@email.com",
                passwordEncoder.encode("Secret1!"));
        user.setActif(true);
        userRepository.save(user);

        LoginRequest req = new LoginRequest("bad.pwd@email.com", "WRONG");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/activate/{token} active un compte avec un token valide")
    void shouldActivateAccountViaController() throws Exception {
        User user = new User("Activate", "Me", "activate.me@email.com",
                passwordEncoder.encode("Secret1!"));
        user.setActif(false);
        user.setActivationToken("token-activate-me-123");
        userRepository.save(user);

        mockMvc.perform(post("/api/auth/activate/" + user.getActivationToken()))
                .andExpect(status().isOk());
    }
}
