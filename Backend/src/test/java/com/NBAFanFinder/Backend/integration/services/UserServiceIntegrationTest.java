package com.NBAFanFinder.Backend.integration.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.NBAFanFinder.Backend.DTOs.Users.AllUsersResponse;
import com.NBAFanFinder.Backend.DTOs.Users.UserResponse;
import com.NBAFanFinder.Backend.Entities.User;
import com.NBAFanFinder.Backend.Repositories.UserRepository;
import com.NBAFanFinder.Backend.Services.UserService;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Tests d'intégration - UserService")
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    public void setUp() {
        User user1 = new User("Pork", "John", "john@pork.com", "password123");
        User user2 = new User("Kirk", "Charlie", "charlie@maga.com", "password123");
        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    @DisplayName("Retourne un utilisateur à partir de son ID")
    void shouldFindUserById() {
        // GIVEN
        User user = userRepository.findByEmail("john@pork.com").orElseThrow();

        // WHEN
        UserResponse result = userService.findById(user.getId());

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.nom()).isEqualTo("Pork");
        assertThat(result.email()).isEqualTo("john@pork.com");
    }

    @Test
    @DisplayName("Retourne la liste de tous les utilisateurs")
    void shouldFindAllUsers() {
        // GIVEN — deux utilisateurs insérés via setUp()

        // WHEN
        List<AllUsersResponse> result = userService.findAll();

        // THEN
        assertThat(result).hasSize(2);
        assertThat(result.get(0).nom()).isEqualTo("Pork");
        assertThat(result.get(0).id()).isPositive();
        assertThat(result.get(1).nom()).isEqualTo("Kirk");
        assertThat(result.get(1).id()).isPositive();
    }

}
