package com.NBAFanFinder.Backend.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "utilisateur")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Accepte en entrée, jamais exposé en sortie
    private String password;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "date_inscription")
    private LocalDate dateInscription;

    @Column(columnDefinition = "TEXT")
    private String biographie;

    @Column(nullable = false)
    private Boolean actif = false;

    @ManyToMany
    @JoinTable(
        name = "ajouter",
        joinColumns = @JoinColumn(name = "id_utilisateur"),
        inverseJoinColumns = @JoinColumn(name = "id_utilisateur_1")
    )
    private Set<User> amis = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "organiser",
        joinColumns = @JoinColumn(name = "id_utilisateur"),
        inverseJoinColumns = @JoinColumn(name = "id_evenement")
    )
    private Set<Event> evenementsOrganises = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "participer",
        joinColumns = @JoinColumn(name = "id_utilisateur"),
        inverseJoinColumns = @JoinColumn(name = "id_evenement")
    )
    private Set<Event> evenementsParticipes = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "supporter",
        joinColumns = @JoinColumn(name = "id_utilisateur"),
        inverseJoinColumns = @JoinColumn(name = "id_equipe")
    )
    private Set<Team> equipesSupporte = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "posseder",
        joinColumns = @JoinColumn(name = "id_utilisateur"),
        inverseJoinColumns = @JoinColumn(name = "id_chat")
    )
    private Set<Chat> chatsPossedes = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rejoindre",
        joinColumns = @JoinColumn(name = "id_utilisateur"),
        inverseJoinColumns = @JoinColumn(name = "id_chat")
    )
    private Set<Chat> chatsRejoints = new HashSet<>();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private Set<Message> messages = new HashSet<>();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private Set<Address> adresses = new HashSet<>();

    // Constructeurs
    public User() {
        this.dateInscription = LocalDate.now();
    }

    public User(String nom, String prenom, String email, String password) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.dateInscription = LocalDate.now();
        this.actif = false;
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    public String getBiographie() {
        return biographie;
    }

    public void setBiographie(String biographie) {
        this.biographie = biographie;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public Set<User> getAmis() {
        return amis;
    }

    public void setAmis(Set<User> amis) {
        this.amis = amis;
    }

    public Set<Event> getEvenementsOrganises() {
        return evenementsOrganises;
    }

    public void setEvenementsOrganises(Set<Event> evenementsOrganises) {
        this.evenementsOrganises = evenementsOrganises;
    }

    public Set<Event> getEvenementsParticipes() {
        return evenementsParticipes;
    }

    public void setEvenementsParticipes(Set<Event> evenementsParticipes) {
        this.evenementsParticipes = evenementsParticipes;
    }

    public Set<Team> getEquipesSupporte() {
        return equipesSupporte;
    }

    public void setEquipesSupporte(Set<Team> equipesSupporte) {
        this.equipesSupporte = equipesSupporte;
    }

    public Set<Chat> getChatsPossedes() {
        return chatsPossedes;
    }

    public void setChatsPossedes(Set<Chat> chatsPossedes) {
        this.chatsPossedes = chatsPossedes;
    }

    public Set<Chat> getChatsRejoints() {
        return chatsRejoints;
    }

    public void setChatsRejoints(Set<Chat> chatsRejoints) {
        this.chatsRejoints = chatsRejoints;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    public Set<Address> getAdresses() {
        return adresses;
    }

    public void setAdresses(Set<Address> adresses) {
        this.adresses = adresses;
    }
}
