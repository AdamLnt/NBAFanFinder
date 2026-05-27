package com.NBAFanFinder.Backend.DTOs;

public record AuthResponse(
    Long id,
    String token,
    String email,
    String nom,
    String prenom,
    String activationToken
) {
    public AuthResponse(Long id, String token, String email, String nom, String prenom) {
        this(id, token, email, nom, prenom, null);
    }
}
