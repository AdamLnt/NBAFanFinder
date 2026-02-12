package com.NBAFanFinder.Backend.DTOs;

public class ErrorResponse {
    private String error;

    // Constructeurs
    public ErrorResponse() {}

    public ErrorResponse(String error) {
        this.error = error;
    }

    // Getters et Setters
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
