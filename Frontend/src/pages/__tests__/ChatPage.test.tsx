import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";

vi.mock("../../services/chatApiService", () => ({
  chatApiService: {
    getChats: vi.fn(),
    getUsers: vi.fn(),
    getMessages: vi.fn(),
    sendMessage: vi.fn(),
    createChat: vi.fn(),
    joinChat: vi.fn(),
    updateChat: vi.fn(),
    removeMember: vi.fn(),
  },
}));
vi.mock("../../services/authService", () => ({
  authService: { getUser: vi.fn(() => ({ id: 1, nom: "Me", prenom: "Moi", email: "me@x.com" })) },
}));
vi.mock("../../services/apiService", () => ({ apiService: { logout: vi.fn() } }));

import { ChatPage } from "../ChatPage";
import { chatApiService } from "../../services/chatApiService";

const mocked = <T,>(fn: T) => fn as unknown as ReturnType<typeof vi.fn>;

const chat = {
  id: 10,
  nom: "Fans Lakers",
  description: "Discussion",
  inviteCode: "code",
  proprietaires: [{ id: 1, nom: "Me", prenom: "Moi" }],
  membres: [{ id: 2, nom: "Martin", prenom: "Paul" }],
};

beforeEach(() => {
  vi.clearAllMocks();
  mocked(chatApiService.getChats).mockResolvedValue([chat]);
  mocked(chatApiService.getUsers).mockResolvedValue([
    { id: 2, nom: "Martin", prenom: "Paul", email: "paul@x.com" },
  ]);
  mocked(chatApiService.getMessages).mockResolvedValue([]);
  mocked(chatApiService.sendMessage).mockResolvedValue(undefined);
  mocked(chatApiService.createChat).mockResolvedValue(undefined);
  mocked(chatApiService.joinChat).mockResolvedValue(undefined);
  mocked(chatApiService.updateChat).mockResolvedValue(undefined);
  mocked(chatApiService.removeMember).mockResolvedValue(undefined);
});

const renderPage = () =>
  render(
    <MemoryRouter initialEntries={["/chat"]}>
      <ChatPage />
    </MemoryRouter>
  );

describe("ChatPage", () => {
  it("charge et affiche les chats dans la sidebar", async () => {
    renderPage();
    expect(await screen.findByText("Fans Lakers")).toBeInTheDocument();
  });

  it("sélectionne un chat puis envoie un message", async () => {
    renderPage();
    fireEvent.click(await screen.findByText("Fans Lakers"));

    await waitFor(() => expect(chatApiService.getMessages).toHaveBeenCalledWith(10));

    const input = await screen.findByPlaceholderText("Écrivez un message...");
    fireEvent.change(input, { target: { value: "Bonjour" } });
    fireEvent.keyDown(input, { key: "Enter" });

    await waitFor(() =>
      expect(chatApiService.sendMessage).toHaveBeenCalledWith({ chatId: 10, texte: "Bonjour" })
    );
  });

  it("ouvre la modale de création de chat", async () => {
    renderPage();
    await screen.findByText("Fans Lakers");
    fireEvent.click(screen.getByTitle(/Créer ou rejoindre/));
    expect(await screen.findByText("Créer un chat")).toBeInTheDocument();
  });

  it("rejoint un chat depuis la modale", async () => {
    renderPage();
    await screen.findByText("Fans Lakers");
    fireEvent.click(screen.getByTitle(/Créer ou rejoindre/));
    fireEvent.click(await screen.findByRole("button", { name: "Rejoindre" }));
    fireEvent.change(screen.getByPlaceholderText(/ID du chat/), { target: { value: "10" } });
    fireEvent.change(screen.getByPlaceholderText(/Code fourni/), { target: { value: "code" } });
    fireEvent.click(screen.getByRole("button", { name: /Rejoindre le chat/ }));
    await waitFor(() =>
      expect(chatApiService.joinChat).toHaveBeenCalledWith({ chatId: 10, inviteCode: "code" })
    );
  });

  it("ouvre les paramètres et met à jour le chat puis retire un membre", async () => {
    renderPage();
    fireEvent.click(await screen.findByText("Fans Lakers"));

    // Le propriétaire (id 1) voit le bouton paramètres dans ChatWindow
    fireEvent.click(await screen.findByTitle("Paramètres du chat"));
    expect(await screen.findByText("Paramètres du chat")).toBeInTheDocument();

    const nameInput = screen
      .getAllByDisplayValue("Fans Lakers")
      .find((el) => el.className.includes("modal__input"))!;
    fireEvent.change(nameInput, { target: { value: "Nouveau nom" } });
    fireEvent.click(screen.getByRole("button", { name: /Enregistrer/ }));
    await waitFor(() =>
      expect(chatApiService.updateChat).toHaveBeenCalledWith(10, {
        nom: "Nouveau nom",
        description: "Discussion",
      })
    );

    fireEvent.click(screen.getByTitle("Retirer ce membre"));
    await waitFor(() => expect(chatApiService.removeMember).toHaveBeenCalledWith(10, 2));
  });
});
