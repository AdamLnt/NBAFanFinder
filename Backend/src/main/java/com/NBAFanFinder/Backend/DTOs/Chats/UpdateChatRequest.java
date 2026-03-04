package com.NBAFanFinder.Backend.DTOs.Chats;

public record UpdateChatRequest(
    String nom,
    String description,
    long requestUserId
) {}
