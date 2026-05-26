package com.NBAFanFinder.Backend.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.NBAFanFinder.Backend.DTOs.AuthResponse;
import com.NBAFanFinder.Backend.DTOs.LoginRequest;
import com.NBAFanFinder.Backend.Entities.User;
import com.NBAFanFinder.Backend.Exceptions.UnauthorizedException;
import com.NBAFanFinder.Backend.Repositories.UserRepository;
import com.NBAFanFinder.Backend.Security.JwtUtil;
import com.NBAFanFinder.Backend.Services.AuthService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Dupont", "Jean", "jean.dupont@email.com", "hashedPassword");
        user.setId(1L);
        user.setActif(true);
    }

    @Test
    @DisplayName("Successful authentification")
    void shouldLoginUserSuccessfully() {
        //GIVEN
        LoginRequest request = new LoginRequest("jean.dupont@email.com", "plainPassword");

        when(userRepository.findByEmail("jean.dupont@email.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plainPassword", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("jean.dupont@email.com")).thenReturn("mock-jwt-token");

        //WHEN
        AuthResponse response = authService.login(request);

        //THEN
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("mock-jwt-token");
        assertThat(response.email()).isEqualTo("jean.dupont@email.com");
        assertThat(response.nom()).isEqualTo("Dupont");
        assertThat(response.prenom()).isEqualTo("Jean");
        assertThat(response.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Invalid Credentials")
    void shouldFailLoginUserWithInvalidCredentials() {
        //GIVEN
        LoginRequest request = new LoginRequest("jean.dupont@email.com", "wrongPassword");

        when(userRepository.findByEmail("jean.dupont@email.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        //WHEN
        assertThatThrownBy(() -> authService.login(request))
                //THEN
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Email ou mot de passe incorrect");
    }
}
