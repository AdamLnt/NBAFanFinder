package com.NBAFanFinder.Backend.DTOs.Chats;

import com.NBAFanFinder.Backend.Entities.User;

public record ChatMemberResponse(Long id, String nom, String prenom) {
    public static ChatMemberResponse from(User user) {
        return new ChatMemberResponse(user.getId(), user.getNom(), user.getPrenom());
    }
}
