import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";

const mockNavigate = vi.fn();
vi.mock("react-router-dom", async (importOriginal) => {
  const actual = await importOriginal<typeof import("react-router-dom")>();
  return { ...actual, useNavigate: () => mockNavigate };
});

// react-leaflet et leaflet ne fonctionnent pas dans jsdom : on les remplace par des stubs.
vi.mock("react-leaflet", () => ({
  MapContainer: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="map">{children}</div>
  ),
  TileLayer: () => null,
  Marker: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="marker">{children}</div>
  ),
  Popup: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  Circle: () => <div data-testid="circle" />,
  useMap: () => ({ flyTo: vi.fn() }),
}));
vi.mock("leaflet", () => ({
  default: { icon: () => ({}), Marker: { prototype: { options: {} } } },
}));

vi.mock("../../services/mapService", () => ({
  mapService: { getUsers: vi.fn(), getMyLocation: vi.fn() },
}));
vi.mock("../../services/teamService", () => ({ teamService: { getAll: vi.fn() } }));
vi.mock("../../services/authService", () => ({
  authService: { getUser: vi.fn(() => ({ id: 1, nom: "Me", prenom: "Moi", email: "me@x.com" })) },
}));
vi.mock("../../services/apiService", () => ({ apiService: { logout: vi.fn() } }));

import { MapPage } from "../MapPage";
import { mapService } from "../../services/mapService";
import { teamService } from "../../services/teamService";

const mocked = <T,>(fn: T) => fn as unknown as ReturnType<typeof vi.fn>;

const otherUser = {
  id: 2,
  nom: "Martin",
  prenom: "Paul",
  ville: "Paris",
  latitude: 48.86,
  longitude: 2.35,
  equipes: [{ id: 1, nom: "Lakers", ville: "LA" }],
};

beforeEach(() => {
  vi.clearAllMocks();
  mocked(teamService.getAll).mockResolvedValue([{ id: 1, nom: "Lakers", ville: "LA" }]);
  mocked(mapService.getMyLocation).mockResolvedValue({
    latitude: 48.86,
    longitude: 2.35,
    ville: "Paris",
  });
  mocked(mapService.getUsers).mockResolvedValue([otherUser]);
});

const renderPage = () =>
  render(
    <MemoryRouter>
      <MapPage />
    </MemoryRouter>
  );

describe("MapPage", () => {
  it("charge les utilisateurs et affiche un marqueur avec popup", async () => {
    renderPage();
    expect(await screen.findByText("Paul Martin")).toBeInTheDocument();
    expect(screen.getByText(/Supporte/)).toBeInTheDocument();
    expect(screen.getByText(/1 utilisateur/)).toBeInTheDocument();
  });

  it("affiche les équipes dans le filtre et recharge au changement", async () => {
    renderPage();
    await screen.findByText("Paul Martin");

    const select = screen.getByRole("combobox");
    fireEvent.change(select, { target: { value: "1" } });

    await waitFor(() => expect(mapService.getUsers).toHaveBeenCalledWith(1));
  });

  it("filtre par distance une fois l'adresse chargée", async () => {
    renderPage();
    await screen.findByText("Paul Martin");
    // homePosition chargé -> les chips de rayon sont activées
    const chip = await waitFor(() => {
      const c = screen.getByRole("button", { name: "5 km" });
      expect(c).not.toBeDisabled();
      return c;
    });
    fireEvent.click(chip);
    // l'utilisateur est à la même position que l'accueil -> reste visible
    expect(screen.getByText("Paul Martin")).toBeInTheDocument();
  });

  it("navigue vers le chat depuis le bouton Discuter", async () => {
    renderPage();
    await screen.findByText("Paul Martin");
    fireEvent.click(screen.getByRole("button", { name: "Discuter" }));
    expect(mockNavigate).toHaveBeenCalledWith("/chat?startWith=2");
  });

  it("affiche une erreur si le chargement des utilisateurs échoue", async () => {
    mocked(mapService.getUsers).mockRejectedValue(new Error("Impossible de charger"));
    renderPage();
    expect(await screen.findByText("Impossible de charger")).toBeInTheDocument();
  });
});
