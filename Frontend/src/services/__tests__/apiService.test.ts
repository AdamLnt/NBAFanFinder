import { describe, it, expect, beforeEach, vi } from "vitest";

// Instance partagée retournée par axios.create(), définie avant l'import du service.
const { mockInstance } = vi.hoisted(() => ({
  mockInstance: {
    get: vi.fn(),
    post: vi.fn(),
    patch: vi.fn(),
    delete: vi.fn(),
  },
}));

vi.mock("axios", () => ({
  default: {
    create: () => mockInstance,
    isAxiosError: (e: unknown) =>
      typeof e === "object" && e !== null && (e as { isAxiosError?: boolean }).isAxiosError === true,
  },
}));

import { apiService } from "../apiService";
import { authService } from "../authService";

const axiosError = (message: string) => ({
  isAxiosError: true,
  response: { data: { error: message } },
});

describe("apiService", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();
  });

  describe("register", () => {
    it("retourne les données en cas de succès", async () => {
      mockInstance.post.mockResolvedValue({ data: { id: 1, email: "a@b.com" } });

      const result = await apiService.register({ email: "a@b.com" } as never);

      expect(result).toEqual({ id: 1, email: "a@b.com" });
      expect(mockInstance.post).toHaveBeenCalledWith("/register", { email: "a@b.com" });
    });

    it("propage le message d'erreur métier de l'API", async () => {
      mockInstance.post.mockRejectedValue(axiosError("Email déjà utilisé"));

      await expect(apiService.register({} as never)).rejects.toThrow("Email déjà utilisé");
    });

    it("renvoie un message générique pour une erreur inconnue", async () => {
      mockInstance.post.mockRejectedValue(new Error("network"));

      await expect(apiService.register({} as never)).rejects.toThrow("Erreur lors de l'inscription");
    });
  });

  describe("login", () => {
    it("retourne la réponse d'authentification", async () => {
      mockInstance.post.mockResolvedValue({ data: { token: "t", email: "a@b.com" } });

      const result = await apiService.login({ email: "a@b.com", password: "x" });

      expect(result.token).toBe("t");
    });

    it("propage le message d'erreur de l'API", async () => {
      mockInstance.post.mockRejectedValue(axiosError("Identifiants invalides"));

      await expect(apiService.login({ email: "", password: "" })).rejects.toThrow(
        "Identifiants invalides"
      );
    });

    it("renvoie un message générique sinon", async () => {
      mockInstance.post.mockRejectedValue(new Error("boom"));

      await expect(apiService.login({ email: "", password: "" })).rejects.toThrow(
        "Erreur lors de la connexion"
      );
    });
  });

  describe("activateAccount", () => {
    it("encode le token dans l'URL", async () => {
      mockInstance.post.mockResolvedValue({ data: null });

      await apiService.activateAccount("abc/def");

      expect(mockInstance.post).toHaveBeenCalledWith("/activate/abc%2Fdef");
    });

    it("propage le message d'erreur de l'API", async () => {
      mockInstance.post.mockRejectedValue(axiosError("Token invalide"));

      await expect(apiService.activateAccount("bad")).rejects.toThrow("Token invalide");
    });

    it("renvoie un message générique sinon", async () => {
      mockInstance.post.mockRejectedValue(new Error("boom"));

      await expect(apiService.activateAccount("x")).rejects.toThrow(
        "Erreur lors de l'activation"
      );
    });
  });

  describe("logout", () => {
    it("appelle l'API et nettoie l'utilisateur local", async () => {
      const clearSpy = vi.spyOn(authService, "clearLocalUser");
      mockInstance.post.mockResolvedValue({ data: null });

      await apiService.logout();

      expect(mockInstance.post).toHaveBeenCalledWith("/logout");
      expect(clearSpy).toHaveBeenCalled();
    });

    it("nettoie l'utilisateur local même si l'API échoue (via finally)", async () => {
      const clearSpy = vi.spyOn(authService, "clearLocalUser");
      mockInstance.post.mockRejectedValue(new Error("offline"));

      await expect(apiService.logout()).rejects.toThrow("offline");
      expect(clearSpy).toHaveBeenCalled();
    });
  });
});
