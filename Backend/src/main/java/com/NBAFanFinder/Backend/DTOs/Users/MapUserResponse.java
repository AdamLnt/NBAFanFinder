package com.NBAFanFinder.Backend.DTOs.Users;

import java.util.List;

import com.NBAFanFinder.Backend.DTOs.Teams.TeamResponse;

public record MapUserResponse(
    Long id,
    String nom,
    String prenom,
    String ville,
    Double latitude,
    Double longitude,
    List<TeamResponse> equipes
) {}
