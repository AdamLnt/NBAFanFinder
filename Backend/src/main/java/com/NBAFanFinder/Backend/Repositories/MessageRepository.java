package com.NBAFanFinder.Backend.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.NBAFanFinder.Backend.Entities.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
    
    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.dateEnvoi ASC")
    List<Message> findByChatId(@Param("chatId") Long chatId);
}
