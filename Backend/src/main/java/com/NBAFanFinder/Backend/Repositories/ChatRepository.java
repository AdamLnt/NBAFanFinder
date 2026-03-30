package com.NBAFanFinder.Backend.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.NBAFanFinder.Backend.Entities.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    // Méthode personnalisée pour récupérer tous les chats d'un utilisateur
    // En utilisant une requête JPQL pour joindre les tables User et Chat
    @Query("SELECT DISTINCT c FROM Chat c LEFT JOIN c.proprietaires p LEFT JOIN c.membres m WHERE p.id = :userId OR m.id = :userId")
    List<Chat> getAllChatsForUser(@Param("userId") long userId);

}