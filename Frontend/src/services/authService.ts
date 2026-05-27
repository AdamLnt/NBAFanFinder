import type { AuthResponse } from "../types/auth";

const USER_KEY = "nba_fan_finder_user";

// Le JWT vit dans un cookie HttpOnly geré par le backend - inaccessible depuis JS.
// On garde uniquement les infos utilisateur non sensibles cote client pour l'UX.

export const authService = {
  setAuth(authResponse: AuthResponse): void {
    localStorage.setItem(
      USER_KEY,
      JSON.stringify({
        id: authResponse.id,
        email: authResponse.email,
        nom: authResponse.nom,
        prenom: authResponse.prenom,
      })
    );
  },

  getUser(): { id: number; email: string; nom: string; prenom: string } | null {
    const userJson = localStorage.getItem(USER_KEY);
    if (!userJson) return null;
    try {
      return JSON.parse(userJson);
    } catch {
      return null;
    }
  },

  // Pas une garantie cryptographique - juste un signal UX cote client.
  // La source de verite reste le cookie HttpOnly. Sur 401, appeler clearLocalUser().
  isAuthenticated(): boolean {
    return this.getUser() !== null;
  },

  clearLocalUser(): void {
    localStorage.removeItem(USER_KEY);
  },
};
