package com.NBAFanFinder.Backend.DTOs.Messages;

import java.time.LocalDateTime;

import com.NBAFanFinder.Backend.Entities.Message;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AllMessagesResponse(
    Long idMessage,
    Long idChat,
    Long idUser,
    String texte,
    @JsonProperty("date_envoi") LocalDateTime dateEnvoi
) {
    public static AllMessagesResponse from(Message message) {
        return new AllMessagesResponse(
            message.getId(),
            message.getChat().getId(),
            message.getUtilisateur().getId(), 
            message.getTexte(), 
            message.getDateEnvoi());
    }
    
}
