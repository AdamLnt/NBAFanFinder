package com.NBAFanFinder.Backend.DTOs.Users;

import java.time.LocalDate;

import com.NBAFanFinder.Backend.Entities.User;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AllUsersResponse(
    long id,
    String nom,
    String prenom,
    String email,
    @JsonProperty("date_naissance") LocalDate dateNaissance
) {
    public static AllUsersResponse from(User user) {
        return new AllUsersResponse(
            user.getId(),
            user.getNom(),
            user.getPrenom(),
            user.getEmail(),
            user.getDateNaissance()
        );
    }
}
