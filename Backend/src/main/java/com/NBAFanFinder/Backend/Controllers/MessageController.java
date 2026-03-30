package com.NBAFanFinder.Backend.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.NBAFanFinder.Backend.DTOs.Messages.AllMessagesResponse;
import com.NBAFanFinder.Backend.DTOs.Messages.SendMessageRequest;
import com.NBAFanFinder.Backend.Services.MessageService;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Message service is running!");
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<AllMessagesResponse>> getMessagesFromChat(@PathVariable Long chatId) {
        return ResponseEntity.ok(messageService.getAllMessagesFromChat(chatId));
    }

    private String getAuthenticatedEmail() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody SendMessageRequest request) {
        messageService.sendMessage(request, getAuthenticatedEmail());
        return ResponseEntity.ok("Message envoyé!");
    }
}
