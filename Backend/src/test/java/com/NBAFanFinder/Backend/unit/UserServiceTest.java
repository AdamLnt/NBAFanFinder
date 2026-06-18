package com.NBAFanFinder.Backend.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.NBAFanFinder.Backend.DTOs.Users.AllUsersResponse;
import com.NBAFanFinder.Backend.DTOs.Users.MapUserResponse;
import com.NBAFanFinder.Backend.DTOs.Users.UserLocationResponse;
import com.NBAFanFinder.Backend.DTOs.Users.UserResponse;
import com.NBAFanFinder.Backend.Entities.Address;
import com.NBAFanFinder.Backend.Entities.Team;
import com.NBAFanFinder.Backend.Entities.User;
import com.NBAFanFinder.Backend.Exceptions.NotFoundException;
import com.NBAFanFinder.Backend.Repositories.UserRepository;
import com.NBAFanFinder.Backend.Services.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - UserService")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Dupont", "Jean", "jean.dupont@email.com", "hashedPassword");
        user.setId(1L);
        user.setActif(true);
    }

    private Address addressFor(User owner, double lat, double lon, String ville) {
        Address address = new Address();
        address.setNumero("10");
        address.setRue("Rue de Rivoli");
        address.setVille(ville);
        address.setCodePostal("75001");
        address.setPays("France");
        address.setLatitude(lat);
        address.setLongitude(lon);
        address.setUtilisateur(owner);
        owner.getAdresses().add(address);
        return address;
    }

    @Test
    @DisplayName("findAll retourne tous les utilisateurs")
    void shouldFindAllUsers() {
        User other = new User("Martin", "Paul", "paul@email.com", "pwd");
        other.setId(2L);
        when(userRepository.findAll()).thenReturn(List.of(user, other));

        List<AllUsersResponse> result = userService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).nom()).isEqualTo("Dupont");
        assertThat(result.get(1).nom()).isEqualTo("Martin");
    }

    @Test
    @DisplayName("findById retourne l'utilisateur quand il existe")
    void shouldFindUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse result = userService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("jean.dupont@email.com");
    }

    @Test
    @DisplayName("findById lève NotFoundException quand l'utilisateur est introuvable")
    void shouldThrowWhenUserByIdNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("findMyLocation retourne la position de la première adresse")
    void shouldFindMyLocation() {
        addressFor(user, 48.8566, 2.3522, "Paris");
        when(userRepository.findByEmail("jean.dupont@email.com")).thenReturn(Optional.of(user));

        UserLocationResponse result = userService.findMyLocation("jean.dupont@email.com");

        assertThat(result.ville()).isEqualTo("Paris");
        assertThat(result.latitude()).isEqualTo(48.8566);
        assertThat(result.longitude()).isEqualTo(2.3522);
    }

    @Test
    @DisplayName("findMyLocation lève NotFoundException si l'utilisateur n'existe pas")
    void shouldThrowWhenLocationUserNotFound() {
        when(userRepository.findByEmail("inconnu@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findMyLocation("inconnu@email.com"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("inconnu@email.com");
    }

    @Test
    @DisplayName("findMyLocation lève NotFoundException si l'utilisateur n'a pas d'adresse")
    void shouldThrowWhenUserHasNoAddress() {
        when(userRepository.findByEmail("jean.dupont@email.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.findMyLocation("jean.dupont@email.com"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Aucune adresse");
    }

    @Test
    @DisplayName("findForMap retourne les utilisateurs actifs avec coordonnées floutées (RGPD)")
    void shouldFindForMapAndBlurCoordinates() {
        Team team = new Team("Lakers", "Los Angeles");
        team.setId(5L);
        user.setEquipesSupporte(Set.of(team));
        addressFor(user, 48.8566, 2.3522, "Paris");
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<MapUserResponse> result = userService.findForMap(null);

        assertThat(result).hasSize(1);
        MapUserResponse mapUser = result.get(0);
        assertThat(mapUser.ville()).isEqualTo("Paris");
        // Arrondi à 2 décimales : 48.8566 -> 48.86 ; 2.3522 -> 2.35
        assertThat(mapUser.latitude()).isEqualTo(48.86);
        assertThat(mapUser.longitude()).isEqualTo(2.35);
        assertThat(mapUser.equipes()).hasSize(1);
    }

    @Test
    @DisplayName("findForMap exclut les utilisateurs inactifs")
    void shouldExcludeInactiveUsersFromMap() {
        user.setActif(false);
        addressFor(user, 48.8566, 2.3522, "Paris");
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<MapUserResponse> result = userService.findForMap(null);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findForMap filtre par équipe quand teamId est fourni")
    void shouldFilterMapByTeam() {
        Team lakers = new Team("Lakers", "Los Angeles");
        lakers.setId(5L);
        user.setEquipesSupporte(Set.of(lakers));
        addressFor(user, 40.0, -73.0, "New York");

        User fanCeltics = new User("Bird", "Larry", "larry@email.com", "pwd");
        fanCeltics.setId(2L);
        fanCeltics.setActif(true);
        Team celtics = new Team("Celtics", "Boston");
        celtics.setId(6L);
        fanCeltics.setEquipesSupporte(Set.of(celtics));
        addressFor(fanCeltics, 42.0, -71.0, "Boston");

        when(userRepository.findAll()).thenReturn(List.of(user, fanCeltics));

        List<MapUserResponse> result = userService.findForMap(5L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).ville()).isEqualTo("New York");
    }

    @Test
    @DisplayName("findForMap ignore les utilisateurs actifs sans adresse")
    void shouldIgnoreActiveUsersWithoutAddress() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<MapUserResponse> result = userService.findForMap(null);

        assertThat(result).isEmpty();
    }
}
