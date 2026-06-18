import { describe, it, expect, beforeEach, vi } from "vitest";

const { mockInstance } = vi.hoisted(() => ({
  mockInstance: {
    get: vi.fn(),
    post: vi.fn(),
    patch: vi.fn(),
    delete: vi.fn(),
  },
}));

vi.mock("axios", () => ({
  default: { create: () => mockInstance },
}));

import { chatApiService } from "../chatApiService";

describe("chatApiService", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("getChats récupère la liste des chats", async () => {
    mockInstance.get.mockResolvedValue({ data: [{ id: 1 }] });

    const result = await chatApiService.getChats();

    expect(result).toEqual([{ id: 1 }]);
    expect(mockInstance.get).toHaveBeenCalledWith("/chat");
  });

  it("createChat poste vers /chat/create", async () => {
    mockInstance.post.mockResolvedValue({ data: null });

    await chatApiService.createChat({ nom: "X" } as never);

    expect(mockInstance.post).toHaveBeenCalledWith("/chat/create", { nom: "X" });
  });

  it("joinChat poste vers /chat/join", async () => {
    mockInstance.post.mockResolvedValue({ data: null });

    await chatApiService.joinChat({ chatId: 1, inviteCode: "c" } as never);

    expect(mockInstance.post).toHaveBeenCalledWith("/chat/join", { chatId: 1, inviteCode: "c" });
  });

  it("getMessages récupère les messages d'un chat", async () => {
    mockInstance.get.mockResolvedValue({ data: [{ id: 9 }] });

    const result = await chatApiService.getMessages(42);

    expect(result).toEqual([{ id: 9 }]);
    expect(mockInstance.get).toHaveBeenCalledWith("/messages/chat/42");
  });

  it("sendMessage poste vers /messages/send", async () => {
    mockInstance.post.mockResolvedValue({ data: null });

    await chatApiService.sendMessage({ chatId: 1, texte: "hi" } as never);

    expect(mockInstance.post).toHaveBeenCalledWith("/messages/send", { chatId: 1, texte: "hi" });
  });

  it("getUsers récupère la liste des utilisateurs", async () => {
    mockInstance.get.mockResolvedValue({ data: [{ id: 1 }] });

    const result = await chatApiService.getUsers();

    expect(result).toEqual([{ id: 1 }]);
    expect(mockInstance.get).toHaveBeenCalledWith("/users");
  });

  it("updateChat patche le chat ciblé", async () => {
    mockInstance.patch.mockResolvedValue({ data: null });

    await chatApiService.updateChat(7, { nom: "New" } as never);

    expect(mockInstance.patch).toHaveBeenCalledWith("/chat/7", { nom: "New" });
  });

  it("removeMember supprime le membre ciblé", async () => {
    mockInstance.delete.mockResolvedValue({ data: null });

    await chatApiService.removeMember(7, 3);

    expect(mockInstance.delete).toHaveBeenCalledWith("/chat/7/members/3");
  });
});
