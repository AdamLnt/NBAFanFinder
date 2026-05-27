import axios from "axios";
import type { AxiosInstance } from "axios";
import type { MapUser, UserLocation } from "../types/map";
import { API_BASE_URL } from "./apiConfig";

const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: { "Content-Type": "application/json" },
  withCredentials: true,
});

export const mapService = {
  async getUsers(teamId?: number | null): Promise<MapUser[]> {
    try {
      const response = await apiClient.get<MapUser[]>("/users/map", {
        params: teamId ? { teamId } : {},
      });
      return response.data;
    } catch {
      throw new Error("Impossible de charger les utilisateurs sur la carte.");
    }
  },

  async getMyLocation(): Promise<UserLocation | null> {
    try {
      const response = await apiClient.get<UserLocation>("/users/me/location");
      return response.data;
    } catch {
      return null;
    }
  },
};
