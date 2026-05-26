package com.NBAFanFinder.Backend.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.NBAFanFinder.Backend.DTOs.AuthResponse;
import com.NBAFanFinder.Backend.DTOs.RegisterRequest;
import com.NBAFanFinder.Backend.Entities.User;
import com.NBAFanFinder.Backend.Exceptions.NotFoundException;
import com.NBAFanFinder.Backend.Repositories.UserRepository;
import com.NBAFanFinder.Backend.Security.JwtUtil;
import com.NBAFanFinder.Backend.Services.AuthService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - AuthService#register et #activate")
public class AuthServiceRegisterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Inscrit un utilisateur avec succès et hash le mot de passe")
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest("Dupont", "Jean", "jean@email.com", "plainPwd", null);
        when(userRepository.existsByEmail("jean@email.com")).thenReturn(false);
        when(passwordEncoder.encode("plainPwd")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(42L);
            return u;
        });

        AuthResponse response = authService.register(request);

        assertThat(response.id()).isEqualTo(42L);
        assertThat(response.email()).isEqualTo("jean@email.com");
        assertThat(response.token()).isNull();
        verify(passwordEncoder).encode("plainPwd");
    }

    @Test
    @DisplayName("Refuse l'inscription si l'email est déjà utilisé")
    void shouldRejectRegisterWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("Dupont", "Jean", "jean@email.com", "plainPwd", null);
        when(userRepository.existsByEmail("jean@email.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("existe déjà");
    }

    @Test
    @DisplayName("Refuse l'inscription si un champ obligatoire est manquant")
    void shouldRejectRegisterWhenFieldMissing() {
        RegisterRequest request = new RegisterRequest(null, "Jean", "jean@email.com", "plainPwd", null);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("obligatoires");
    }

    @Test
    @DisplayName("Active un compte utilisateur existant")
    void shouldActivateUserSuccessfully() {
        User user = new User("Dupont", "Jean", "jean@email.com", "hashed");
        user.setId(1L);
        user.setActif(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        authService.activate(1L);

        assertThat(user.getActif()).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Lève NotFoundException quand on active un id inconnu")
    void shouldThrowWhenActivateUnknownUser() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.activate(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Utilisateur non trouvé");
    }
}
