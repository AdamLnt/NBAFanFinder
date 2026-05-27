package com.NBAFanFinder.Backend.DTOs.Users;

import com.NBAFanFinder.Backend.Entities.User;

// RGPD : la date de naissance ne sort PAS dans le listing public.
// Elle reste accessible sur le profil dedie (UserResponse#findById) si necessaire.
public record AllUsersResponse(
    long id,
    String nom,
    String prenom,
    String email
) {
    public static AllUsersResponse from(User user) {
        return new AllUsersResponse(
            user.getId(),
            user.getNom(),
            user.getPrenom(),
            user.getEmail()
        );
    }
}
