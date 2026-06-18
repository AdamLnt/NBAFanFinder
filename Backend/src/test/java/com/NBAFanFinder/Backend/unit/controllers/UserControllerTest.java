package com.NBAFanFinder.Backend.unit.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.NBAFanFinder.Backend.Controllers.UserController;
import com.NBAFanFinder.Backend.DTOs.Users.AllUsersResponse;
import com.NBAFanFinder.Backend.DTOs.Users.MapUserResponse;
import com.NBAFanFinder.Backend.DTOs.Users.UserLocationResponse;
import com.NBAFanFinder.Backend.DTOs.Users.UserResponse;
import com.NBAFanFinder.Backend.Services.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - UserController")
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setAuth() {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("jean@email.com", null));
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("GET /api/users renvoie la liste des utilisateurs")
    void shouldGetAllUsers() {
        when(userService.findAll()).thenReturn(List.of(new AllUsersResponse(1L, "Dupont", "Jean", "jean@email.com")));

        ResponseEntity<List<AllUsersResponse>> response = userController.getAllUsers();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("GET /api/users/ping renvoie Pong!")
    void shouldPing() {
        assertThat(userController.ping().getBody()).isEqualTo("Pong!");
    }

    @Test
    @DisplayName("GET /api/users/map délègue au service avec teamId")
    void shouldGetUsersForMap() {
        when(userService.findForMap(5L)).thenReturn(List.of(
            new MapUserResponse(1L, "Dupont", "Jean", "Paris", 48.86, 2.35, List.of())));

        ResponseEntity<List<MapUserResponse>> response = userController.getUsersForMap(5L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("GET /api/users/me/location utilise l'email authentifié")
    void shouldGetMyLocation() {
        when(userService.findMyLocation("jean@email.com"))
            .thenReturn(new UserLocationResponse(48.86, 2.35, "Paris"));

        ResponseEntity<UserLocationResponse> response = userController.getMyLocation();

        assertThat(response.getBody().ville()).isEqualTo("Paris");
    }

    @Test
    @DisplayName("GET /api/users/{id} renvoie l'utilisateur")
    void shouldGetUserById() {
        when(userService.findById(1L)).thenReturn(
            new UserResponse(1L, "Dupont", "Jean", "jean@email.com", null, null, null, true));

        ResponseEntity<UserResponse> response = userController.getUserById(1L);

        assertThat(response.getBody().email()).isEqualTo("jean@email.com");
    }
}
