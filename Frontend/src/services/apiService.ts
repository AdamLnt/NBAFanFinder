import axios from "axios";
import type { AxiosInstance } from "axios";
import type { LoginRequest, RegisterRequest, AuthResponse } from "../types/auth";
import { authService } from "./authService";

// URL de base de l'API - à ajuster selon votre configuration
const API_BASE_URL = "http://localhost:8080/api/auth";

// Créer une instance Axios avec configuration par défaut
const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Intercepteur pour ajouter le token aux requêtes
apiClient.interceptors.request.use(
  (config) => {
    const token = authService.getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export const apiService = {
  // Inscription
  async register(data: RegisterRequest): Promise<AuthResponse> {
    try {
      const response = await apiClient.post<AuthResponse>("/register", data);
      return response.data;
    } catch (error: any) {
      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error("Erreur lors de l'inscription. Veuillez réessayer.");
    }
  },

  // Connexion
  async login(data: LoginRequest): Promise<AuthResponse> {
    try {
      const response = await apiClient.post<AuthResponse>("/login", data);
      return response.data;
    } catch (error: any) {
      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error("Erreur lors de la connexion. Vérifiez vos identifiants.");
    }
  },

  // Activation du compte
  async activateAccount(userId: number): Promise<void> {
    try {
      await apiClient.post(`/activate/${userId}`);
    } catch (error: any) {
      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error("Erreur lors de l'activation du compte.");
    }
  },
};
