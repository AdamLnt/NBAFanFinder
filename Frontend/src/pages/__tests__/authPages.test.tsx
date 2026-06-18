import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { MemoryRouter, Routes, Route } from "react-router-dom";

const mockNavigate = vi.fn();
vi.mock("react-router-dom", async (importOriginal) => {
  const actual = await importOriginal<typeof import("react-router-dom")>();
  return { ...actual, useNavigate: () => mockNavigate };
});

vi.mock("../../services/apiService", () => ({
  apiService: {
    login: vi.fn(),
    logout: vi.fn().mockResolvedValue(undefined),
    activateAccount: vi.fn(),
  },
}));
vi.mock("../../services/authService", () => ({
  authService: {
    isAuthenticated: vi.fn(),
    getUser: vi.fn(),
    setAuth: vi.fn(),
    clearLocalUser: vi.fn(),
  },
}));

import { LoginPage } from "../LoginPage";
import { HomePage } from "../HomePage";
import { ProfilePage } from "../ProfilePage";
import { ActivationPage } from "../ActivationPage";
import { apiService } from "../../services/apiService";
import { authService } from "../../services/authService";

const mocked = <T,>(fn: T) => fn as unknown as ReturnType<typeof vi.fn>;

beforeEach(() => {
  vi.clearAllMocks();
});

describe("LoginPage", () => {
  it("affiche une erreur si les champs sont vides", () => {
    render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>
    );
    fireEvent.click(screen.getByRole("button", { name: /Se connecter/ }));
    expect(screen.getByText("Veuillez remplir tous les champs")).toBeInTheDocument();
  });

  it("connecte l'utilisateur et redirige vers /map", async () => {
    mocked(apiService.login).mockResolvedValue({ id: 1, email: "a@b.com", nom: "D", prenom: "J" });
    render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByPlaceholderText("exemple@email.com"), {
      target: { value: "a@b.com", name: "email" },
    });
    fireEvent.change(screen.getByPlaceholderText("Entrez votre mot de passe"), {
      target: { value: "secret", name: "password" },
    });
    fireEvent.click(screen.getByRole("button", { name: /Se connecter/ }));

    await waitFor(() => expect(authService.setAuth).toHaveBeenCalled());
    expect(mockNavigate).toHaveBeenCalledWith("/map");
  });

  it("affiche le message d'erreur en cas d'échec de connexion", async () => {
    mocked(apiService.login).mockRejectedValue(new Error("Identifiants invalides"));
    render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByPlaceholderText("exemple@email.com"), {
      target: { value: "a@b.com", name: "email" },
    });
    fireEvent.change(screen.getByPlaceholderText("Entrez votre mot de passe"), {
      target: { value: "bad", name: "password" },
    });
    fireEvent.click(screen.getByRole("button", { name: /Se connecter/ }));

    expect(await screen.findByText("Identifiants invalides")).toBeInTheDocument();
  });

  it("navigue vers /register via le lien", () => {
    render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>
    );
    fireEvent.click(screen.getByText("Cliquez ici"));
    expect(mockNavigate).toHaveBeenCalledWith("/register");
  });
});

describe("HomePage", () => {
  it("redirige vers /login si non authentifié", () => {
    mocked(authService.isAuthenticated).mockReturnValue(false);
    const { container } = render(
      <MemoryRouter>
        <HomePage />
      </MemoryRouter>
    );
    expect(container).toBeEmptyDOMElement();
    expect(mockNavigate).toHaveBeenCalledWith("/login");
  });

  it("affiche les informations utilisateur quand authentifié", async () => {
    mocked(authService.isAuthenticated).mockReturnValue(true);
    mocked(authService.getUser).mockReturnValue({
      id: 1,
      email: "a@b.com",
      nom: "Dupont",
      prenom: "Jean",
    });
    render(
      <MemoryRouter>
        <HomePage />
      </MemoryRouter>
    );

    expect(screen.getByText("Bienvenue !")).toBeInTheDocument();
    expect(screen.getByText("Dupont")).toBeInTheDocument();

    fireEvent.click(screen.getByRole("button", { name: "Se déconnecter" }));
    await waitFor(() => expect(apiService.logout).toHaveBeenCalled());
  });
});

describe("ProfilePage", () => {
  it("affiche le profil de l'utilisateur", () => {
    mocked(authService.getUser).mockReturnValue({
      id: 1,
      email: "a@b.com",
      nom: "Dupont",
      prenom: "Jean",
    });
    render(
      <MemoryRouter>
        <ProfilePage />
      </MemoryRouter>
    );
    expect(screen.getByText("Jean Dupont")).toBeInTheDocument();
    expect(screen.getByText("a@b.com")).toBeInTheDocument();
  });

  it("affiche des tirets quand aucun utilisateur", () => {
    mocked(authService.getUser).mockReturnValue(null);
    render(
      <MemoryRouter>
        <ProfilePage />
      </MemoryRouter>
    );
    expect(screen.getAllByText("—").length).toBeGreaterThan(0);
  });
});

describe("ActivationPage", () => {
  const renderAt = (token: string) =>
    render(
      <MemoryRouter initialEntries={[`/activation/${token}`]}>
        <Routes>
          <Route path="/activation/:token" element={<ActivationPage />} />
        </Routes>
      </MemoryRouter>
    );

  it("active le compte au clic et affiche le succès", async () => {
    mocked(apiService.activateAccount).mockResolvedValue(undefined);
    renderAt("tok-123");

    fireEvent.click(screen.getByText(/Activer mon compte/));
    expect(await screen.findByText(/Compte activé avec succès/)).toBeInTheDocument();
    expect(apiService.activateAccount).toHaveBeenCalledWith("tok-123");
  });

  it("affiche une erreur si l'activation échoue", async () => {
    mocked(apiService.activateAccount).mockRejectedValue(new Error("Token invalide"));
    renderAt("bad");

    fireEvent.click(screen.getByText(/Activer mon compte/));
    expect(await screen.findByText("Token invalide")).toBeInTheDocument();
  });
});
