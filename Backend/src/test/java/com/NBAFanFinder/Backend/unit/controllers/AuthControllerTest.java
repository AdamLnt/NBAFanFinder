package com.NBAFanFinder.Backend.unit.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.NBAFanFinder.Backend.Controllers.AuthController;
import com.NBAFanFinder.Backend.DTOs.AuthResponse;
import com.NBAFanFinder.Backend.DTOs.LoginRequest;
import com.NBAFanFinder.Backend.Security.AuthCookieService;
import com.NBAFanFinder.Backend.Services.AuthService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - AuthController")
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private AuthCookieService cookieService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("POST /api/auth/login pose le cookie quand un token est présent")
    void shouldLoginAndSetCookie() {
        LoginRequest request = new LoginRequest("jean@email.com", "pwd");
        AuthResponse body = new AuthResponse(1L, "jwt-token", "jean@email.com", "Dupont", "Jean");
        when(authService.login(request)).thenReturn(body);

        ResponseEntity<AuthResponse> result = authController.login(request, response);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().token()).isEqualTo("jwt-token");
        verify(cookieService).setAuthCookie(response, "jwt-token");
    }

    @Test
    @DisplayName("POST /api/auth/login ne pose pas de cookie sans token")
    void shouldLoginWithoutCookieWhenNoToken() {
        LoginRequest request = new LoginRequest("jean@email.com", "pwd");
        AuthResponse body = new AuthResponse(1L, null, "jean@email.com", "Dupont", "Jean");
        when(authService.login(request)).thenReturn(body);

        authController.login(request, response);

        verify(cookieService, never()).setAuthCookie(response, null);
    }

    @Test
    @DisplayName("POST /api/auth/logout supprime le cookie et renvoie 204")
    void shouldLogout() {
        ResponseEntity<Void> result = authController.logout(response);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(cookieService).clearAuthCookie(response);
    }

    @Test
    @DisplayName("POST /api/auth/activate/{token} active un compte et renvoie 200")
    void shouldActivate() {
        ResponseEntity<Void> result = authController.activate("token-123");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(authService).activate("token-123");
    }

    @Test
    @DisplayName("GET /api/auth/ping renvoie un message de service")
    void shouldPing() {
        assertThat(authController.ping().getBody()).contains("running");
    }
}
