import { describe, it, expect, beforeEach, vi } from "vitest";

const { mockGet } = vi.hoisted(() => ({ mockGet: vi.fn() }));

vi.mock("axios", () => ({
  default: { get: mockGet },
}));

import { teamService } from "../teamService";

describe("teamService", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("getAll retourne la liste des équipes", async () => {
    mockGet.mockResolvedValue({ data: [{ id: 1, nom: "Celtics", ville: "Boston" }] });

    const result = await teamService.getAll();

    expect(result).toEqual([{ id: 1, nom: "Celtics", ville: "Boston" }]);
    expect(mockGet).toHaveBeenCalledWith(expect.stringContaining("/teams"), {
      withCredentials: true,
    });
  });

  it("getAll lève une erreur explicite en cas d'échec", async () => {
    mockGet.mockRejectedValue(new Error("500"));

    await expect(teamService.getAll()).rejects.toThrow("Impossible de charger la liste des équipes");
  });
});
