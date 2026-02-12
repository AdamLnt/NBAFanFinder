package com.NBAFanFinder.Backend.Entities;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "chat")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chat")
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "nombre_participants", nullable = false)
    private Integer nombreParticipants = 0;

    @ManyToMany(mappedBy = "chatsPossedes")
    private Set<User> proprietaires = new HashSet<>();

    @ManyToMany(mappedBy = "chatsRejoints")
    private Set<User> membres = new HashSet<>();

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private Set<Message> messages = new HashSet<>();

    // Constructeurs
    public Chat() {
    }

    public Chat(String nom, String description) {
        this.nom = nom;
        this.description = description;
        this.nombreParticipants = 0;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNombreParticipants() {
        return nombreParticipants;
    }

    public void setNombreParticipants(Integer nombreParticipants) {
        this.nombreParticipants = nombreParticipants;
    }

    public Set<User> getProprietaires() {
        return proprietaires;
    }

    public void setProprietaires(Set<User> proprietaires) {
        this.proprietaires = proprietaires;
    }

    public Set<User> getMembres() {
        return membres;
    }

    public void setMembres(Set<User> membres) {
        this.membres = membres;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }
}
