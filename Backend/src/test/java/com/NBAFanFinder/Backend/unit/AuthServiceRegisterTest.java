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

import com.NBAFanFinder.Backend.DTOs.AddressRequest;
import com.NBAFanFinder.Backend.DTOs.AuthResponse;
import com.NBAFanFinder.Backend.DTOs.RegisterRequest;
import com.NBAFanFinder.Backend.Entities.User;
import com.NBAFanFinder.Backend.Exceptions.NotFoundException;
import com.NBAFanFinder.Backend.Repositories.AddressRepository;
import com.NBAFanFinder.Backend.Repositories.TeamRepository;
import com.NBAFanFinder.Backend.Repositories.UserRepository;
import com.NBAFanFinder.Backend.Security.JwtUtil;
import com.NBAFanFinder.Backend.Services.AuthService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - AuthService#register et #activate")
public class AuthServiceRegisterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private static final AddressRequest VALID_ADDRESS = new AddressRequest(
        "10", "Rue de Rivoli", "Paris", "75001", "France", 48.8566, 2.3522
    );

    @Test
    @DisplayName("Inscrit un utilisateur avec succès et hash le mot de passe")
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest(
            "Dupont", "Jean", "jean@email.com", "PlainPwd1@xx", null, VALID_ADDRESS, null
        );
        when(userRepository.existsByEmail("jean@email.com")).thenReturn(false);
        when(passwordEncoder.encode("PlainPwd1@xx")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(42L);
            return u;
        });

        AuthResponse response = authService.register(request);

        assertThat(response.id()).isEqualTo(42L);
        assertThat(response.email()).isEqualTo("jean@email.com");
        assertThat(response.token()).isNull();
        verify(passwordEncoder).encode("PlainPwd1@xx");
    }

    @Test
    @DisplayName("Refuse l'inscription si l'email est déjà utilisé")
    void shouldRejectRegisterWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest(
            "Dupont", "Jean", "jean@email.com", "PlainPwd1@xx", null, VALID_ADDRESS, null
        );
        when(userRepository.existsByEmail("jean@email.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("existe déjà");
    }

    @Test
    @DisplayName("Refuse l'inscription si un champ obligatoire est manquant")
    void shouldRejectRegisterWhenFieldMissing() {
        RegisterRequest request = new RegisterRequest(
            null, "Jean", "jean@email.com", "PlainPwd1@xx", null, VALID_ADDRESS, null
        );

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("obligatoires");
    }

    @Test
    @DisplayName("Refuse l'inscription si l'adresse est manquante")
    void shouldRejectRegisterWhenAddressMissing() {
        RegisterRequest request = new RegisterRequest(
            "Dupont", "Jean", "jean@email.com", "PlainPwd1@xx", null, null, null
        );

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("adresse");
    }

    @Test
    @DisplayName("Refuse l'inscription si le mot de passe ne respecte pas la politique")
    void shouldRejectRegisterWhenPasswordTooWeak() {
        RegisterRequest request = new RegisterRequest(
            "Dupont", "Jean", "jean@email.com", "weakpwd", null, VALID_ADDRESS, null
        );

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("12 caractères");
    }

    @Test
    @DisplayName("Active un compte utilisateur existant via son token")
    void shouldActivateUserSuccessfully() {
        String token = "activation-token-abc";
        User user = new User("Dupont", "Jean", "jean@email.com", "hashed");
        user.setId(1L);
        user.setActif(false);
        user.setActivationToken(token);
        when(userRepository.findByActivationToken(token)).thenReturn(Optional.of(user));

        authService.activate(token);

        assertThat(user.getActif()).isTrue();
        assertThat(user.getActivationToken()).isNull();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Lève NotFoundException quand on active un token inconnu")
    void shouldThrowWhenActivateUnknownToken() {
        when(userRepository.findByActivationToken("does-not-exist")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.activate("does-not-exist"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Token d'activation");
    }
}
