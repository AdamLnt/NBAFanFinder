package com.NBAFanFinder.Backend.DTOs.Chats;

import java.util.List;

public record CreateChatRequest(String nom, String description, long userId, List<Long> membresIds, List<Long> proprietairesIds) { }
