package com.NBAFanFinder.Backend.Entities;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "equipe")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipe")
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String ville;

    @ManyToMany(mappedBy = "equipesSupporte")
    private Set<User> supporters = new HashSet<>();

    // Constructeurs
    public Team() {
    }

    public Team(String nom, String ville) {
        this.nom = nom;
        this.ville = ville;
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

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public Set<User> getSupporters() {
        return supporters;
    }

    public void setSupporters(Set<User> supporters) {
        this.supporters = supporters;
    }
}
