import type { Team } from "./team";

export interface MapUser {
  id: number;
  nom: string;
  prenom: string;
  ville: string;
  latitude: number;
  longitude: number;
  equipes: Team[];
}

export interface UserLocation {
  latitude: number;
  longitude: number;
  ville: string;
}

export interface GeocodingResult {
  displayName: string;
  numero: string;
  rue: string;
  ville: string;
  codePostal: string;
  pays: string;
  latitude: number;
  longitude: number;
}
