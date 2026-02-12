import type { AuthResponse } from "../types/auth";

const TOKEN_KEY = "nba_fan_finder_token";
const USER_KEY = "nba_fan_finder_user";

export const authService = {
  // Stocker le token et les informations utilisateur
  setAuth(authResponse: AuthResponse): void {
    if (authResponse.token) {
      localStorage.setItem(TOKEN_KEY, authResponse.token);
    }
    localStorage.setItem(USER_KEY, JSON.stringify({
      email: authResponse.email,
      nom: authResponse.nom,
      prenom: authResponse.prenom,
    }));
  },

  // Récupérer le token
  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  },

  // Récupérer les informations utilisateur
  getUser(): { email: string; nom: string; prenom: string } | null {
    const userJson = localStorage.getItem(USER_KEY);
    if (!userJson) return null;
    try {
      return JSON.parse(userJson);
    } catch {
      return null;
    }
  },

  // Vérifier si l'utilisateur est connecté
  isAuthenticated(): boolean {
    return this.getToken() !== null;
  },

  // Déconnexion
  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  },

  // Obtenir le header Authorization pour les requêtes
  getAuthHeader(): { Authorization: string } | {} {
    const token = this.getToken();
    return token ? { Authorization: `Bearer ${token}` } : {};
  },
};
