import { describe, it, expect, beforeEach, vi } from "vitest";

const { mockInstance } = vi.hoisted(() => ({
  mockInstance: { get: vi.fn(), post: vi.fn(), patch: vi.fn(), delete: vi.fn() },
}));

vi.mock("axios", () => ({
  default: { create: () => mockInstance },
}));

import { mapService } from "../mapService";

describe("mapService", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("getUsers passe le teamId en paramètre quand fourni", async () => {
    mockInstance.get.mockResolvedValue({ data: [{ id: 1 }] });

    const result = await mapService.getUsers(5);

    expect(result).toEqual([{ id: 1 }]);
    expect(mockInstance.get).toHaveBeenCalledWith("/users/map", { params: { teamId: 5 } });
  });

  it("getUsers envoie des params vides quand teamId est absent", async () => {
    mockInstance.get.mockResolvedValue({ data: [] });

    await mapService.getUsers();

    expect(mockInstance.get).toHaveBeenCalledWith("/users/map", { params: {} });
  });

  it("getUsers lève une erreur explicite en cas d'échec", async () => {
    mockInstance.get.mockRejectedValue(new Error("500"));

    await expect(mapService.getUsers()).rejects.toThrow("Impossible de charger les utilisateurs");
  });

  it("getMyLocation retourne la localisation", async () => {
    mockInstance.get.mockResolvedValue({ data: { ville: "Paris" } });

    const result = await mapService.getMyLocation();

    expect(result).toEqual({ ville: "Paris" });
  });

  it("getMyLocation retourne null en cas d'erreur", async () => {
    mockInstance.get.mockRejectedValue(new Error("404"));

    expect(await mapService.getMyLocation()).toBeNull();
  });
});
