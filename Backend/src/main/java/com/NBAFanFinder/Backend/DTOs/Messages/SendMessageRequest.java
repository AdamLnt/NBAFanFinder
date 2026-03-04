package com.NBAFanFinder.Backend.DTOs.Messages;

public record SendMessageRequest(long chatId, long userId, String texte) {}
