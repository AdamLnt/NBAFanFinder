package com.NBAFanFinder.Backend.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddressRequest(
    String numero,
    String rue,
    String ville,
    @JsonProperty("code_postal") String codePostal,
    String pays,
    Double latitude,
    Double longitude
) {}
