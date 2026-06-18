import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";

const mockNavigate = vi.fn();
vi.mock("react-router-dom", async (importOriginal) => {
  const actual = await importOriginal<typeof import("react-router-dom")>();
  return { ...actual, useNavigate: () => mockNavigate };
});

vi.mock("../../services/apiService", () => ({ apiService: { register: vi.fn(), logout: vi.fn() } }));
vi.mock("../../services/teamService", () => ({ teamService: { getAll: vi.fn() } }));
vi.mock("../../services/geocodingService", () => ({ geocodingService: { search: vi.fn() } }));

import { RegisterPage } from "../RegisterPage";
import { apiService } from "../../services/apiService";
import { teamService } from "../../services/teamService";
import { geocodingService } from "../../services/geocodingService";

const mocked = <T,>(fn: T) => fn as unknown as ReturnType<typeof vi.fn>;

const address = {
  displayName: "10 Rue de Rivoli, Paris",
  numero: "10",
  rue: "Rue de Rivoli",
  ville: "Paris",
  codePostal: "75001",
  pays: "France",
  latitude: 48.8566,
  longitude: 2.3522,
};

beforeEach(() => {
  vi.clearAllMocks();
  mocked(teamService.getAll).mockResolvedValue([{ id: 1, nom: "Lakers", ville: "LA" }]);
  mocked(geocodingService.search).mockResolvedValue([address]);
});

const fillField = (placeholder: string, value: string, name: string) =>
  fireEvent.change(screen.getByPlaceholderText(placeholder), { target: { value, name } });

const renderPage = () => render(<MemoryRouter><RegisterPage /></MemoryRouter>);

describe("RegisterPage", () => {
  it("charge et affiche les équipes, et permet de les sélectionner", async () => {
    renderPage();
    const teamChip = await screen.findByRole("button", { name: "Lakers" });
    fireEvent.click(teamChip);
    expect(teamChip.className).toContain("team-chip--selected");
  });

  it("affiche une erreur si des champs obligatoires manquent", async () => {
    renderPage();
    fireEvent.click(screen.getByRole("button", { name: /S'inscrire/ }));
    expect(await screen.findByText(/remplir tous les champs/)).toBeInTheDocument();
  });

  it("affiche une erreur si les mots de passe diffèrent", async () => {
    renderPage();
    fillField("Entrez votre nom", "Dupont", "nom");
    fillField("Entrez votre prénom", "Jean", "prenom");
    fillField("exemple@email.com", "a@b.com", "email");
    fillField("Créez un mot de passe", "Password123!@xx", "password");
    fillField("Confirmez le mot de passe", "different", "confirmation");
    fireEvent.click(screen.getByRole("button", { name: /S'inscrire/ }));
    expect(await screen.findByText(/ne correspondent pas/)).toBeInTheDocument();
  });

  it("affiche une erreur si le mot de passe est trop faible", async () => {
    renderPage();
    fillField("Entrez votre nom", "Dupont", "nom");
    fillField("Entrez votre prénom", "Jean", "prenom");
    fillField("exemple@email.com", "a@b.com", "email");
    fillField("Créez un mot de passe", "weak", "password");
    fillField("Confirmez le mot de passe", "weak", "confirmation");
    fireEvent.click(screen.getByRole("button", { name: /S'inscrire/ }));
    expect(await screen.findByText(/doit contenir au moins 12 caractères/)).toBeInTheDocument();
  });

  it("affiche une erreur si aucune adresse n'est sélectionnée", async () => {
    renderPage();
    fillField("Entrez votre nom", "Dupont", "nom");
    fillField("Entrez votre prénom", "Jean", "prenom");
    fillField("exemple@email.com", "a@b.com", "email");
    fillField("Créez un mot de passe", "Password123!@xx", "password");
    fillField("Confirmez le mot de passe", "Password123!@xx", "confirmation");
    fireEvent.click(screen.getByRole("button", { name: /S'inscrire/ }));
    expect(await screen.findByText(/sélectionner une adresse/)).toBeInTheDocument();
  });

  it("inscrit l'utilisateur et redirige vers la page d'activation", async () => {
    mocked(apiService.register).mockResolvedValue({ activationToken: "tok-abc" });
    renderPage();

    fillField("Entrez votre nom", "Dupont", "nom");
    fillField("Entrez votre prénom", "Jean", "prenom");
    fillField("exemple@email.com", "a@b.com", "email");
    fillField("Créez un mot de passe", "Password123!@xx", "password");
    fillField("Confirmez le mot de passe", "Password123!@xx", "confirmation");

    // Géocodage debouncé : on tape l'adresse, la suggestion apparaît après ~400ms
    fireEvent.change(screen.getByPlaceholderText(/Commencez à taper/), {
      target: { value: "10 Rue de Rivoli" },
    });
    fireEvent.click(await screen.findByText("10 Rue de Rivoli, Paris"));
    expect(await screen.findByText(/Adresse sélectionnée/)).toBeInTheDocument();

    fireEvent.click(screen.getByRole("button", { name: /S'inscrire/ }));

    await waitFor(() => expect(apiService.register).toHaveBeenCalled());
    expect(mockNavigate).toHaveBeenCalledWith("/activation/tok-abc");
  });
});
