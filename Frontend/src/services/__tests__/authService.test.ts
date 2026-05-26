import { describe, it, expect, beforeEach } from "vitest";
import { authService } from "../authService";
import type { AuthResponse } from "../../types/auth";

describe("authService", () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it("setAuth stocke le token et l'utilisateur dans le localStorage", () => {
    const response: AuthResponse = {
      id: 1,
      token: "tok-123",
      email: "user@email.com",
      nom: "Doe",
      prenom: "John",
    };

    authService.setAuth(response);

    expect(authService.getToken()).toBe("tok-123");
    expect(authService.getUser()).toEqual({
      id: 1,
      email: "user@email.com",
      nom: "Doe",
      prenom: "John",
    });
  });

  it("isAuthenticated renvoie true quand un token est présent", () => {
    authService.setAuth({
      id: 1,
      token: "tok",
      email: "user@email.com",
      nom: "Doe",
      prenom: "John",
    });

    expect(authService.isAuthenticated()).toBe(true);
  });

  it("isAuthenticated renvoie false sans token", () => {
    expect(authService.isAuthenticated()).toBe(false);
  });

  it("logout supprime le token et l'utilisateur", () => {
    authService.setAuth({
      id: 1,
      token: "tok",
      email: "user@email.com",
      nom: "Doe",
      prenom: "John",
    });

    authService.logout();

    expect(authService.getToken()).toBeNull();
    expect(authService.getUser()).toBeNull();
  });

  it("getAuthHeader retourne un header Authorization si token présent", () => {
    authService.setAuth({
      id: 1,
      token: "abc",
      email: "user@email.com",
      nom: "Doe",
      prenom: "John",
    });

    expect(authService.getAuthHeader()).toEqual({ Authorization: "Bearer abc" });
  });

  it("getAuthHeader retourne un objet vide sans token", () => {
    expect(authService.getAuthHeader()).toEqual({});
  });

  it("getUser renvoie null si le JSON stocké est corrompu", () => {
    localStorage.setItem("nba_fan_finder_user", "{not valid json");

    expect(authService.getUser()).toBeNull();
  });
});
