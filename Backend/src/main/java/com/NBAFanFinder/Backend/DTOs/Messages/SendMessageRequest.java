package com.NBAFanFinder.Backend.DTOs.Messages;

public record SendMessageRequest(long chatId, String texte) {}
