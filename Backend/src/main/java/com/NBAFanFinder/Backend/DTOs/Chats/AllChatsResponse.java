package com.NBAFanFinder.Backend.DTOs.Chats;

import java.util.List;

import com.NBAFanFinder.Backend.Entities.Chat;

public record AllChatsResponse(
    Long id,
    String nom,
    String description,
    List<ChatMemberResponse> proprietaires,
    List<ChatMemberResponse> membres
) {
    public static AllChatsResponse from(Chat chat) {
        return new AllChatsResponse(
            chat.getId(),
            chat.getNom(),
            chat.getDescription(),
            chat.getProprietaires().stream().map(ChatMemberResponse::from).toList(),
            chat.getMembres().stream().map(ChatMemberResponse::from).toList()
        );
    }

}
