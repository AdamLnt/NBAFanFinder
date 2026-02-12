package com.NBAFanFinder.Backend.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "adresse")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_adresse")
    private Long id;

    @Column(nullable = false)
    private String numero;

    @Column(nullable = false)
    private String rue;

    @Column(nullable = false)
    private String ville;

    @Column(name = "code_postal", nullable = false)
    private String codePostal;

    @Column(nullable = false)
    private String pays;

    @ManyToOne
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private User utilisateur;

    // Constructeurs
    public Address() {
    }

    public Address(String numero, String rue, String ville, String codePostal, String pays, User utilisateur) {
        this.numero = numero;
        this.rue = rue;
        this.ville = ville;
        this.codePostal = codePostal;
        this.pays = pays;
        this.utilisateur = utilisateur;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public User getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(User utilisateur) {
        this.utilisateur = utilisateur;
    }
}
