package com.NBAFanFinder.Backend.unit.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.NBAFanFinder.Backend.Controllers.TeamController;
import com.NBAFanFinder.Backend.DTOs.Teams.TeamResponse;
import com.NBAFanFinder.Backend.Services.TeamService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - TeamController")
public class TeamControllerTest {

    @Mock
    private TeamService teamService;

    @InjectMocks
    private TeamController teamController;

    @Test
    @DisplayName("GET /api/teams renvoie la liste des équipes")
    void shouldGetAllTeams() {
        when(teamService.findAll()).thenReturn(List.of(new TeamResponse(1L, "Celtics", "Boston")));

        ResponseEntity<List<TeamResponse>> response = teamController.getAllTeams();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).nom()).isEqualTo("Celtics");
    }
}
