import axios from "axios";
import type { Team } from "../types/team";

const API_BASE_URL = "http://localhost:8080/api";

export const teamService = {
  async getAll(): Promise<Team[]> {
    try {
      const response = await axios.get<Team[]>(`${API_BASE_URL}/teams`);
      return response.data;
    } catch {
      throw new Error("Impossible de charger la liste des équipes.");
    }
  },
};
