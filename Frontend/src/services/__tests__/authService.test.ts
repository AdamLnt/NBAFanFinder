import { describe, it, expect, beforeEach } from "vitest";
import { authService } from "../authService";
import type { AuthResponse } from "../../types/auth";

describe("authService (cookie-based session)", () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it("setAuth stocke uniquement les infos utilisateur (pas le token)", () => {
    const response: AuthResponse = {
      id: 1,
      token: "tok-123",
      email: "user@email.com",
      nom: "Doe",
      prenom: "John",
    };

    authService.setAuth(response);

    expect(authService.getUser()).toEqual({
      id: 1,
      email: "user@email.com",
      nom: "Doe",
      prenom: "John",
    });
    // Le token ne doit JAMAIS etre stocke cote client - il vit dans le cookie HttpOnly
    expect(localStorage.getItem("nba_fan_finder_token")).toBeNull();
  });

  it("isAuthenticated renvoie true quand l'utilisateur est en localStorage", () => {
    authService.setAuth({
      id: 1,
      token: "tok",
      email: "user@email.com",
      nom: "Doe",
      prenom: "John",
    });

    expect(authService.isAuthenticated()).toBe(true);
  });

  it("isAuthenticated renvoie false sans utilisateur", () => {
    expect(authService.isAuthenticated()).toBe(false);
  });

  it("clearLocalUser supprime l'utilisateur du localStorage", () => {
    authService.setAuth({
      id: 1,
      token: "tok",
      email: "user@email.com",
      nom: "Doe",
      prenom: "John",
    });

    authService.clearLocalUser();

    expect(authService.getUser()).toBeNull();
  });

  it("getUser renvoie null si le JSON stocke est corrompu", () => {
    localStorage.setItem("nba_fan_finder_user", "{not valid json");

    expect(authService.getUser()).toBeNull();
  });
});
