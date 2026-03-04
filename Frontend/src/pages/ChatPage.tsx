import { useState, useEffect, useRef } from "react";
import { Header } from "../components/Header";
import { Footer } from "../components/Footer";
import { ChatSidebar } from "../components/chat/ChatSidebar";
import { ChatWindow } from "../components/chat/ChatWindow";
import { CreateChatModal } from "../components/chat/CreateChatModal";
import { chatApiService } from "../services/chatApiService";
import { authService } from "../services/authService";
import type { Chat, Message, User } from "../types/chat";
import "../styles/ChatPage.css";

export const ChatPage = () => {
  const user = authService.getUser();
  const userId = user?.id ?? 0;

  const [chats, setChats] = useState<Chat[]>([]);
  const [selectedChat, setSelectedChat] = useState<Chat | null>(null);
  const [messages, setMessages] = useState<Message[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [loadingChats, setLoadingChats] = useState(false);
  const [loadingMessages, setLoadingMessages] = useState(false);
  const selectedChatRef = useRef<Chat | null>(null);

  useEffect(() => {
    selectedChatRef.current = selectedChat;
  }, [selectedChat]);

  useEffect(() => {
    if (!selectedChat) return;
    const interval = setInterval(async () => {
      if (!selectedChatRef.current) return;
      try {
        const data = await chatApiService.getMessages(selectedChatRef.current.id);
        setMessages(data);
      } catch {
        // silencieux
      }
    }, 3000);
    return () => clearInterval(interval);
  }, [selectedChat?.id]);

  const loadChats = async () => {
    setLoadingChats(true);
    try {
      const data = await chatApiService.getChats(userId);
      setChats(data);
    } catch {
      // erreur silencieuse
    } finally {
      setLoadingChats(false);
    }
  };

  const loadMessages = async (chatId: number) => {
    setLoadingMessages(true);
    try {
      const data = await chatApiService.getMessages(chatId);
      setMessages(data);
    } catch {
      setMessages([]);
    } finally {
      setLoadingMessages(false);
    }
  };

  useEffect(() => {
    if (userId) {
      loadChats();
      chatApiService.getUsers().then((all) =>
        setUsers(all.filter((u) => u.id !== userId))
      );
    }
  }, [userId]);

  const handleSelectChat = (chat: Chat) => {
    setSelectedChat(chat);
    loadMessages(chat.id);
  };

  const handleSendMessage = async (text: string) => {
    if (!selectedChat) return;
    await chatApiService.sendMessage({ chatId: selectedChat.id, userId, texte: text });
    await loadMessages(selectedChat.id);
  };

  const handleCreateChat = async (nom: string, description: string, membresIds: number[]) => {
    await chatApiService.createChat({ nom, description, userId, membresIds });
    setModalOpen(false);
    const updated = await chatApiService.getChats(userId);
    setChats(updated);
  };

  const isDirectChatWith = (c: Chat, otherUserId: number) => {
    const total = (c.proprietaires?.length ?? 0) + (c.membres?.length ?? 0);
    if (total !== 2) return false;
    return (
      (c.proprietaires.some((p) => p.id === userId) && c.membres.some((m) => m.id === otherUserId)) ||
      (c.proprietaires.some((p) => p.id === otherUserId) && c.membres.some((m) => m.id === userId))
    );
  };

  const handleStartChat = async (targetUser: User) => {
    const existing = chats.find((c) => isDirectChatWith(c, targetUser.id));
    if (existing) {
      handleSelectChat(existing);
      return;
    }
    await chatApiService.createChat({
      nom: `${targetUser.prenom} ${targetUser.nom}`,
      description: "Chat direct",
      userId,
      membresIds: [targetUser.id],
    });
    const updated = await chatApiService.getChats(userId);
    setChats(updated);
    const newChat = updated.find((c) => isDirectChatWith(c, targetUser.id));
    if (newChat) handleSelectChat(newChat);
  };

  const handleJoinChat = async (chatId: number) => {
    await chatApiService.joinChat({ chatId, userId });
    setModalOpen(false);
    const updated = await chatApiService.getChats(userId);
    setChats(updated);
  };

  return (
    <div className="chat-page">
      <Header />
      <main className="chat-main">
        <ChatSidebar
          chats={chats}
          selectedChatId={selectedChat?.id ?? null}
          onSelectChat={handleSelectChat}
          onOpenModal={() => setModalOpen(true)}
          onNewConversation={() => setSelectedChat(null)}
          loading={loadingChats}
        />
        <ChatWindow
          chat={selectedChat}
          messages={messages}
          currentUserId={userId}
          onSendMessage={handleSendMessage}
          loading={loadingMessages}
          users={users}
          onStartChat={handleStartChat}
        />
      </main>
      {modalOpen && (
        <CreateChatModal
          currentUserId={userId}
          onClose={() => setModalOpen(false)}
          onCreateChat={handleCreateChat}
          onJoinChat={handleJoinChat}
        />
      )}
      <Footer />
    </div>
  );
};
