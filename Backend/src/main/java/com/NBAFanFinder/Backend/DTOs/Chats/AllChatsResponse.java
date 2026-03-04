package com.NBAFanFinder.Backend.DTOs.Chats;

import com.NBAFanFinder.Backend.Entities.Chat;

public record AllChatsResponse(
    String nom,
    String description
) {
    public static AllChatsResponse from(Chat chat) {
        return new AllChatsResponse(
            chat.getNom(),
            chat.getDescription()
        );
    }
    
}
