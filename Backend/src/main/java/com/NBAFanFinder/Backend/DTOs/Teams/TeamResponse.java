package com.NBAFanFinder.Backend.DTOs.Teams;

import com.NBAFanFinder.Backend.Entities.Team;

public record TeamResponse(Long id, String nom, String ville) {
    public static TeamResponse from(Team team) {
        return new TeamResponse(team.getId(), team.getNom(), team.getVille());
    }
}
