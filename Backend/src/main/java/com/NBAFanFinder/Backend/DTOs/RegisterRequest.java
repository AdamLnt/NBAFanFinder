package com.NBAFanFinder.Backend.DTOs;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterRequest(
    String nom,
    String prenom,
    String email,
    String password,
    @JsonProperty("date_naissance") LocalDate dateNaissance,
    AddressRequest adresse,
    @JsonProperty("equipes_supportees_ids") List<Long> equipesSupporteesIds
) {}
