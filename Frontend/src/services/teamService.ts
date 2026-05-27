import axios from "axios";
import type { Team } from "../types/team";
import { API_BASE_URL } from "./apiConfig";

export const teamService = {
  async getAll(): Promise<Team[]> {
    try {
      const response = await axios.get<Team[]>(`${API_BASE_URL}/teams`, {
        withCredentials: true,
      });
      return response.data;
    } catch {
      throw new Error("Impossible de charger la liste des équipes.");
    }
  },
};
