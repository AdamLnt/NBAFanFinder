package com.NBAFanFinder.Backend.DTOs;

public record AuthResponse(Long id, String token, String email, String nom, String prenom) {}
