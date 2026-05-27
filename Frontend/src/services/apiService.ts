import axios from "axios";
import type { AxiosInstance } from "axios";
import type { LoginRequest, RegisterRequest, AuthResponse } from "../types/auth";
import { API_BASE_URL } from "./apiConfig";
import { authService } from "./authService";

const apiClient: AxiosInstance = axios.create({
  baseURL: `${API_BASE_URL}/auth`,
  headers: { "Content-Type": "application/json" },
  withCredentials: true,
});

export const apiService = {
  async register(data: RegisterRequest): Promise<AuthResponse> {
    try {
      const response = await apiClient.post<AuthResponse>("/register", data);
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error) && error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error("Erreur lors de l'inscription. Veuillez réessayer.");
    }
  },

  async login(data: LoginRequest): Promise<AuthResponse> {
    try {
      const response = await apiClient.post<AuthResponse>("/login", data);
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error) && error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error("Erreur lors de la connexion. Vérifiez vos identifiants.");
    }
  },

  async activateAccount(activationToken: string): Promise<void> {
    try {
      await apiClient.post(`/activate/${encodeURIComponent(activationToken)}`);
    } catch (error) {
      if (axios.isAxiosError(error) && error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error("Erreur lors de l'activation du compte.");
    }
  },

  async logout(): Promise<void> {
    try {
      await apiClient.post("/logout");
    } finally {
      authService.clearLocalUser();
    }
  },
};
