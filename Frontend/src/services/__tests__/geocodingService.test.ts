import { describe, it, expect, beforeEach, vi } from "vitest";

const { mockGet } = vi.hoisted(() => ({ mockGet: vi.fn() }));

vi.mock("axios", () => ({
  default: { get: mockGet },
}));

import { geocodingService } from "../geocodingService";

const nominatimItem = {
  display_name: "10 Rue de Rivoli, Paris",
  lat: "48.8566",
  lon: "2.3522",
  address: {
    house_number: "10",
    road: "Rue de Rivoli",
    city: "Paris",
    postcode: "75001",
    country: "France",
  },
};

describe("geocodingService", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe("search", () => {
    it("retourne [] sans appel réseau si la requête fait moins de 3 caractères", async () => {
      const result = await geocodingService.search("ab");

      expect(result).toEqual([]);
      expect(mockGet).not.toHaveBeenCalled();
    });

    it("mappe les résultats Nominatim vers GeocodingResult", async () => {
      mockGet.mockResolvedValue({ data: [nominatimItem] });

      const result = await geocodingService.search("Rue de Rivoli");

      expect(result).toHaveLength(1);
      expect(result[0]).toMatchObject({
        numero: "10",
        rue: "Rue de Rivoli",
        ville: "Paris",
        codePostal: "75001",
        pays: "France",
        latitude: 48.8566,
        longitude: 2.3522,
      });
    });

    it("gère une adresse partielle avec des valeurs par défaut", async () => {
      mockGet.mockResolvedValue({
        data: [{ display_name: "x", lat: "1", lon: "2" }],
      });

      const result = await geocodingService.search("query test");

      expect(result[0]).toMatchObject({ numero: "", rue: "", ville: "", pays: "" });
    });

    it("utilise les villes alternatives (town/village/municipality)", async () => {
      mockGet.mockResolvedValue({
        data: [{ display_name: "x", lat: "1", lon: "2", address: { town: "Bourg" } }],
      });

      const result = await geocodingService.search("query test");

      expect(result[0].ville).toBe("Bourg");
    });

    it("retourne [] en cas d'erreur réseau", async () => {
      mockGet.mockRejectedValue(new Error("network"));

      expect(await geocodingService.search("query test")).toEqual([]);
    });
  });

  describe("reverse", () => {
    it("retourne un GeocodingResult mappé", async () => {
      mockGet.mockResolvedValue({ data: nominatimItem });

      const result = await geocodingService.reverse(48.8566, 2.3522);

      expect(result).toMatchObject({ ville: "Paris", codePostal: "75001" });
    });

    it("retourne null en cas d'erreur", async () => {
      mockGet.mockRejectedValue(new Error("network"));

      expect(await geocodingService.reverse(0, 0)).toBeNull();
    });
  });
});
