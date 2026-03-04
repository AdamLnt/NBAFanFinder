package com.NBAFanFinder.Backend.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.NBAFanFinder.Backend.DTOs.Chats.AllChatsResponse;
import com.NBAFanFinder.Backend.DTOs.Chats.CreateChatRequest;
import com.NBAFanFinder.Backend.DTOs.Chats.JoinChatRequest;
import com.NBAFanFinder.Backend.DTOs.Chats.UpdateChatRequest;
import com.NBAFanFinder.Backend.Services.ChatService;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Chat service is running!");
    }

    @GetMapping
    public ResponseEntity<List<AllChatsResponse>> getAllChats(@RequestParam long userId) {
        return ResponseEntity.ok(chatService.getAllChatsFromUser(userId));
    }

    @PostMapping("/create")
    public ResponseEntity<String> createChat(@RequestBody CreateChatRequest request) {
        chatService.createChat(request);
        return ResponseEntity.status(201).body("Chat créé!");
    }

    @PostMapping("/join")
    public ResponseEntity<String> joinChat(@RequestBody JoinChatRequest request) {
        chatService.joinChat(request);
        return ResponseEntity.status(200).body("Utilisateur ajouté au chat!");
    }

    @PatchMapping("/{chatId}")
    public ResponseEntity<String> updateChat(@PathVariable long chatId, @RequestBody UpdateChatRequest request) {
        chatService.updateChat(chatId, request);
        return ResponseEntity.ok("Chat mis à jour!");
    }

    @DeleteMapping("/{chatId}/members/{memberId}")
    public ResponseEntity<String> removeMember(
            @PathVariable long chatId,
            @PathVariable long memberId,
            @RequestParam long requestUserId) {
        chatService.removeMember(chatId, memberId, requestUserId);
        return ResponseEntity.ok("Membre retiré du chat!");
    }
}
