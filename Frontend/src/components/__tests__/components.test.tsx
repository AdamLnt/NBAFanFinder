import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";

import { Footer } from "../Footer";
import { Logo } from "../Logo";
import { NavLink } from "../NavLink";
import { Header } from "../Header";
import { ProtectedRoute } from "../ProtectedRoute";
import { ChatIcon } from "../icons/ChatIcon";
import { MapIcon } from "../icons/MapIcon";
import { Card, Title, Label, Field, SubmitButton, LinkText } from "../FormComponents";
import { authService } from "../../services/authService";
import { apiService } from "../../services/apiService";

vi.mock("../../services/apiService", () => ({
  apiService: { logout: vi.fn().mockResolvedValue(undefined) },
}));

describe("Composants présentationnels", () => {
  it("Footer affiche le copyright et un bouton de contact", () => {
    render(<Footer />);
    expect(screen.getByText(/Tous droits réservés/)).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /Me contacter/ })).toBeInTheDocument();
  });

  it("Logo se rend sans erreur", () => {
    const { container } = render(<Logo />);
    expect(container.querySelector("svg")).toBeInTheDocument();
  });

  it("ChatIcon et MapIcon rendent un SVG", () => {
    const { container: c1 } = render(<ChatIcon />);
    const { container: c2 } = render(<MapIcon />);
    expect(c1.querySelector("svg")).toBeInTheDocument();
    expect(c2.querySelector("svg")).toBeInTheDocument();
  });

  it("NavLink déclenche onClick et applique la classe active", () => {
    const onClick = vi.fn();
    const { rerender } = render(<NavLink label="Carte" onClick={onClick} active />);
    const btn = screen.getByRole("button", { name: "Carte" });
    expect(btn.className).toContain("nav-link--active");
    fireEvent.click(btn);
    expect(onClick).toHaveBeenCalled();

    rerender(<NavLink label="Carte" onClick={onClick} />);
    expect(screen.getByRole("button", { name: "Carte" }).className).not.toContain("--active");
  });

  it("FormComponents se rendent et propagent les événements", () => {
    const onChange = vi.fn();
    const onSubmit = vi.fn();
    const onLink = vi.fn();
    render(
      <Card>
        <Title>Titre</Title>
        <Label>Étiquette</Label>
        <Field label="Email" name="email" value="a@b.com" onChange={onChange} />
        <SubmitButton onClick={onSubmit}>Valider</SubmitButton>
        <LinkText onClick={onLink}>Pas de compte ?</LinkText>
      </Card>
    );
    expect(screen.getByText("Titre")).toBeInTheDocument();
    expect(screen.getByText("Étiquette")).toBeInTheDocument();

    fireEvent.change(screen.getByDisplayValue("a@b.com"), { target: { value: "x" } });
    expect(onChange).toHaveBeenCalled();

    fireEvent.click(screen.getByRole("button", { name: "Valider" }));
    expect(onSubmit).toHaveBeenCalled();

    fireEvent.click(screen.getByText("Cliquez ici"));
    expect(onLink).toHaveBeenCalled();
  });
});

describe("ProtectedRoute", () => {
  beforeEach(() => vi.restoreAllMocks());

  it("rend les enfants quand l'utilisateur est authentifié", () => {
    vi.spyOn(authService, "isAuthenticated").mockReturnValue(true);
    render(
      <MemoryRouter>
        <ProtectedRoute>
          <p>Contenu protégé</p>
        </ProtectedRoute>
      </MemoryRouter>
    );
    expect(screen.getByText("Contenu protégé")).toBeInTheDocument();
  });

  it("redirige quand l'utilisateur n'est pas authentifié", () => {
    vi.spyOn(authService, "isAuthenticated").mockReturnValue(false);
    render(
      <MemoryRouter initialEntries={["/secret"]}>
        <ProtectedRoute>
          <p>Contenu protégé</p>
        </ProtectedRoute>
      </MemoryRouter>
    );
    expect(screen.queryByText("Contenu protégé")).not.toBeInTheDocument();
  });
});

describe("Header", () => {
  beforeEach(() => vi.clearAllMocks());

  it("affiche les boutons de navigation et déclenche le logout", async () => {
    render(
      <MemoryRouter>
        <Header />
      </MemoryRouter>
    );
    const logoutBtn = screen.getByRole("button", { name: /Déconnexion/ });
    fireEvent.click(logoutBtn);
    await waitFor(() => expect(apiService.logout).toHaveBeenCalled());
  });
});
