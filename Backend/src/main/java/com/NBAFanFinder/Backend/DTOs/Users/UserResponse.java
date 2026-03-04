package com.NBAFanFinder.Backend.DTOs.Users;

import java.time.LocalDate;

import com.NBAFanFinder.Backend.Entities.User;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UserResponse(
    long id, 
    String nom, 
    String prenom, 
    String email, 
    @JsonProperty("date_naissance") LocalDate dateNaissance, 
    String biographie, 
    @JsonProperty("date_inscription") LocalDate dateInscription,
    boolean actif
) {
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getNom(),
            user.getPrenom(),
            user.getEmail(),
            user.getDateNaissance(),
            user.getBiographie(),
            user.getDateInscription(),
            user.getActif()
        );
    }
    
}
