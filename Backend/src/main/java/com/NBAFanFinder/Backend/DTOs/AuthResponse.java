package com.NBAFanFinder.Backend.DTOs;

public class AuthResponse {
    private Long id;
    private String token;
    private String email;
    private String nom;
    private String prenom;

    // Constructeurs
    public AuthResponse() {}

    public AuthResponse(Long id, String token, String email, String nom, String prenom) {
        this.id = id;
        this.token = token;
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
}
