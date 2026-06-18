package com.NBAFanFinder.Backend.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.NBAFanFinder.Backend.DTOs.Teams.TeamResponse;
import com.NBAFanFinder.Backend.Entities.Team;
import com.NBAFanFinder.Backend.Repositories.TeamRepository;
import com.NBAFanFinder.Backend.Services.TeamService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - TeamService")
public class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamService teamService;

    @Test
    @DisplayName("findAll retourne les équipes triées par ville")
    void shouldReturnAllTeams() {
        Team boston = new Team("Celtics", "Boston");
        boston.setId(1L);
        Team losAngeles = new Team("Lakers", "Los Angeles");
        losAngeles.setId(2L);
        when(teamRepository.findAllByOrderByVilleAsc()).thenReturn(List.of(boston, losAngeles));

        List<TeamResponse> result = teamService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).ville()).isEqualTo("Boston");
        assertThat(result.get(0).nom()).isEqualTo("Celtics");
        assertThat(result.get(1).ville()).isEqualTo("Los Angeles");
    }

    @Test
    @DisplayName("findAll retourne une liste vide quand il n'y a pas d'équipe")
    void shouldReturnEmptyListWhenNoTeams() {
        when(teamRepository.findAllByOrderByVilleAsc()).thenReturn(List.of());

        assertThat(teamService.findAll()).isEmpty();
    }
}
