package com.NBAFanFinder.Backend.Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "evenement")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evenement")
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String lieu;

    @Column(name = "date_evenement", nullable = false)
    private LocalDateTime dateEvenement;

    @Column(name = "nombre_places", nullable = false)
    private Integer nombrePlaces;

    @ManyToMany(mappedBy = "evenementsOrganises")
    private Set<User> organisateurs = new HashSet<>();

    @ManyToMany(mappedBy = "evenementsParticipes")
    private Set<User> participants = new HashSet<>();

    // Constructeurs
    public Event() {
    }

    public Event(String nom, String lieu, LocalDateTime dateEvenement, Integer nombrePlaces) {
        this.nom = nom;
        this.lieu = lieu;
        this.dateEvenement = dateEvenement;
        this.nombrePlaces = nombrePlaces;
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

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public LocalDateTime getDateEvenement() {
        return dateEvenement;
    }

    public void setDateEvenement(LocalDateTime dateEvenement) {
        this.dateEvenement = dateEvenement;
    }

    public Integer getNombrePlaces() {
        return nombrePlaces;
    }

    public void setNombrePlaces(Integer nombrePlaces) {
        this.nombrePlaces = nombrePlaces;
    }

    public Set<User> getOrganisateurs() {
        return organisateurs;
    }

    public void setOrganisateurs(Set<User> organisateurs) {
        this.organisateurs = organisateurs;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }
}
