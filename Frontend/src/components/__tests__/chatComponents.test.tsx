import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";

import { ChatSidebar } from "../chat/ChatSidebar";
import { ChatWindow } from "../chat/ChatWindow";
import { CreateChatModal } from "../chat/CreateChatModal";
import { EditChatModal } from "../chat/EditChatModal";
import type { Chat, Message, User } from "../../types/chat";
import { chatApiService } from "../../services/chatApiService";

vi.mock("../../services/chatApiService", () => ({
  chatApiService: { getUsers: vi.fn() },
}));

const owner = { id: 1, nom: "Dupont", prenom: "Jean" };
const member = { id: 2, nom: "Martin", prenom: "Paul" };

const chat: Chat = {
  id: 10,
  nom: "Fans Lakers",
  description: "Discussion Lakers",
  inviteCode: "code-123",
  proprietaires: [owner],
  membres: [member],
};

const users: User[] = [
  { id: 2, nom: "Martin", prenom: "Paul", email: "paul@email.com" },
  { id: 3, nom: "Bernard", prenom: "Luc", email: "luc@email.com" },
];

describe("ChatSidebar", () => {
  it("affiche l'état de chargement", () => {
    render(
      <ChatSidebar
        chats={[]}
        selectedChatId={null}
        onSelectChat={vi.fn()}
        onOpenModal={vi.fn()}
        onNewConversation={vi.fn()}
        loading={true}
      />
    );
    expect(screen.getByText("Chargement...")).toBeInTheDocument();
  });

  it("affiche un message vide quand il n'y a pas de chat", () => {
    render(
      <ChatSidebar
        chats={[]}
        selectedChatId={null}
        onSelectChat={vi.fn()}
        onOpenModal={vi.fn()}
        onNewConversation={vi.fn()}
        loading={false}
      />
    );
    expect(screen.getByText(/Aucun chat/)).toBeInTheDocument();
  });

  it("liste les chats et déclenche la sélection / les actions", () => {
    const onSelect = vi.fn();
    const onOpenModal = vi.fn();
    const onNew = vi.fn();
    render(
      <ChatSidebar
        chats={[chat]}
        selectedChatId={10}
        onSelectChat={onSelect}
        onOpenModal={onOpenModal}
        onNewConversation={onNew}
        loading={false}
      />
    );
    fireEvent.click(screen.getByText("Fans Lakers"));
    expect(onSelect).toHaveBeenCalledWith(chat);

    fireEvent.click(screen.getByTitle(/Créer ou rejoindre/));
    expect(onOpenModal).toHaveBeenCalled();

    fireEvent.click(screen.getByTitle(/Nouvelle conversation/));
    expect(onNew).toHaveBeenCalled();
  });
});

describe("ChatWindow", () => {
  it("affiche la grille d'utilisateurs quand aucun chat n'est sélectionné", async () => {
    const onStartChat = vi.fn().mockResolvedValue(undefined);
    render(
      <ChatWindow
        chat={null}
        messages={[]}
        currentUserId={1}
        onSendMessage={vi.fn()}
        loading={false}
        users={users}
        onStartChat={onStartChat}
        onOpenSettings={vi.fn()}
      />
    );
    expect(screen.getByText("Démarrer une conversation")).toBeInTheDocument();
    fireEvent.click(screen.getByText("Paul Martin"));
    await waitFor(() => expect(onStartChat).toHaveBeenCalled());
  });

  it("affiche 'aucun utilisateur' quand la liste est vide", () => {
    render(
      <ChatWindow
        chat={null}
        messages={[]}
        currentUserId={1}
        onSendMessage={vi.fn()}
        loading={false}
        users={[]}
        onStartChat={vi.fn()}
        onOpenSettings={vi.fn()}
      />
    );
    expect(screen.getByText(/Aucun autre utilisateur/)).toBeInTheDocument();
  });

  it("affiche les messages et envoie un nouveau message", async () => {
    const onSend = vi.fn().mockResolvedValue(undefined);
    const messages: Message[] = [
      {
        idMessage: 1,
        idChat: 10,
        idUser: 2,
        texte: "Salut tout le monde",
        date_envoi: [2024, 1, 1, 10, 30, 0],
        nom_utilisateur: "Martin",
        prenom_utilisateur: "Paul",
      },
    ];
    render(
      <ChatWindow
        chat={chat}
        messages={messages}
        currentUserId={1}
        onSendMessage={onSend}
        loading={false}
        users={users}
        onStartChat={vi.fn()}
        onOpenSettings={vi.fn()}
      />
    );
    expect(screen.getByText("Salut tout le monde")).toBeInTheDocument();

    const input = screen.getByPlaceholderText("Écrivez un message...");
    fireEvent.change(input, { target: { value: "Ma réponse" } });
    fireEvent.keyDown(input, { key: "Enter" });
    await waitFor(() => expect(onSend).toHaveBeenCalledWith("Ma réponse"));
  });

  it("affiche le bouton paramètres pour un propriétaire et ouvre le panneau membres", () => {
    const onSettings = vi.fn();
    render(
      <ChatWindow
        chat={chat}
        messages={[]}
        currentUserId={1}
        onSendMessage={vi.fn()}
        loading={false}
        users={users}
        onStartChat={vi.fn()}
        onOpenSettings={onSettings}
      />
    );
    expect(screen.getByText(/Aucun message/)).toBeInTheDocument();

    fireEvent.click(screen.getByTitle("Paramètres du chat"));
    expect(onSettings).toHaveBeenCalled();

    fireEvent.click(screen.getByTitle("Voir les membres"));
    expect(screen.getByText("Administrateurs")).toBeInTheDocument();
    expect(screen.getByText("Jean Dupont")).toBeInTheDocument();
  });

  it("affiche l'état de chargement des messages", () => {
    render(
      <ChatWindow
        chat={chat}
        messages={[]}
        currentUserId={99}
        onSendMessage={vi.fn()}
        loading={true}
        users={users}
        onStartChat={vi.fn()}
        onOpenSettings={vi.fn()}
      />
    );
    expect(screen.getByText(/Chargement des messages/)).toBeInTheDocument();
  });
});

describe("CreateChatModal", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    (chatApiService.getUsers as ReturnType<typeof vi.fn>).mockResolvedValue(users);
  });

  it("charge les utilisateurs et crée un chat", async () => {
    const onCreate = vi.fn().mockResolvedValue(undefined);
    render(
      <CreateChatModal
        currentUserId={1}
        onClose={vi.fn()}
        onCreateChat={onCreate}
        onJoinChat={vi.fn()}
      />
    );
    await waitFor(() => expect(screen.getByText("Luc Bernard")).toBeInTheDocument());

    fireEvent.change(screen.getByPlaceholderText(/Fans des Lakers/), {
      target: { value: "Mon chat" },
    });
    fireEvent.click(screen.getByText("Luc Bernard"));
    fireEvent.click(screen.getByRole("button", { name: /Créer le chat/ }));

    await waitFor(() => expect(onCreate).toHaveBeenCalledWith("Mon chat", "", [3]));
  });

  it("filtre les utilisateurs via la recherche", async () => {
    render(
      <CreateChatModal
        currentUserId={1}
        onClose={vi.fn()}
        onCreateChat={vi.fn()}
        onJoinChat={vi.fn()}
      />
    );
    await waitFor(() => expect(screen.getByText("Luc Bernard")).toBeInTheDocument());

    fireEvent.change(screen.getByPlaceholderText(/Rechercher par nom/), {
      target: { value: "paul" },
    });
    expect(screen.queryByText("Luc Bernard")).not.toBeInTheDocument();
    expect(screen.getByText("Paul Martin")).toBeInTheDocument();
  });

  it("bascule sur l'onglet Rejoindre et rejoint un chat", async () => {
    const onJoin = vi.fn().mockResolvedValue(undefined);
    render(
      <CreateChatModal
        currentUserId={1}
        onClose={vi.fn()}
        onCreateChat={vi.fn()}
        onJoinChat={onJoin}
      />
    );
    fireEvent.click(screen.getByRole("button", { name: "Rejoindre" }));

    fireEvent.change(screen.getByPlaceholderText(/ID du chat/), { target: { value: "42" } });
    fireEvent.change(screen.getByPlaceholderText(/Code fourni/), { target: { value: "abc" } });
    fireEvent.click(screen.getByRole("button", { name: /Rejoindre le chat/ }));

    await waitFor(() => expect(onJoin).toHaveBeenCalledWith(42, "abc"));
  });

  it("ferme la modale via le bouton de fermeture", async () => {
    const onClose = vi.fn();
    render(
      <CreateChatModal
        currentUserId={1}
        onClose={onClose}
        onCreateChat={vi.fn()}
        onJoinChat={vi.fn()}
      />
    );
    fireEvent.click(screen.getByText("✕"));
    expect(onClose).toHaveBeenCalled();
  });
});

describe("EditChatModal", () => {
  it("enregistre les modifications quand les infos changent", async () => {
    const onUpdate = vi.fn().mockResolvedValue(undefined);
    render(
      <EditChatModal
        chat={chat}
        onClose={vi.fn()}
        onUpdateChat={onUpdate}
        onRemoveMember={vi.fn()}
      />
    );
    const saveBtn = screen.getByRole("button", { name: /Enregistrer/ });
    expect(saveBtn).toBeDisabled();

    fireEvent.change(screen.getAllByDisplayValue("Fans Lakers")[0], {
      target: { value: "Nouveau nom" },
    });
    expect(saveBtn).not.toBeDisabled();
    fireEvent.click(saveBtn);
    await waitFor(() => expect(onUpdate).toHaveBeenCalledWith("Nouveau nom", "Discussion Lakers"));
  });

  it("retire un membre", async () => {
    const onRemove = vi.fn().mockResolvedValue(undefined);
    render(
      <EditChatModal
        chat={chat}
        onClose={vi.fn()}
        onUpdateChat={vi.fn()}
        onRemoveMember={onRemove}
      />
    );
    expect(screen.getByText("Paul Martin")).toBeInTheDocument();
    fireEvent.click(screen.getByTitle("Retirer ce membre"));
    await waitFor(() => expect(onRemove).toHaveBeenCalledWith(2));
  });
});
