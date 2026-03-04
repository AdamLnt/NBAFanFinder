import axios from "axios";
import type { AxiosInstance } from "axios";
import { authService } from "./authService";
import type { Chat, Message, User, CreateChatRequest, JoinChatRequest, SendMessageRequest, UpdateChatRequest } from "../types/chat";

const chatClient: AxiosInstance = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: { "Content-Type": "application/json" },
});

chatClient.interceptors.request.use(
  (config) => {
    const token = authService.getToken();
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
  },
  (error) => Promise.reject(error)
);

export const chatApiService = {
  async getChats(userId: number): Promise<Chat[]> {
    const res = await chatClient.get<Chat[]>(`/chat?userId=${userId}`);
    return res.data;
  },

  async createChat(data: CreateChatRequest): Promise<void> {
    await chatClient.post("/chat/create", data);
  },

  async joinChat(data: JoinChatRequest): Promise<void> {
    await chatClient.post("/chat/join", data);
  },

  async getMessages(chatId: number): Promise<Message[]> {
    const res = await chatClient.get<Message[]>(`/messages/chat/${chatId}`);
    return res.data;
  },

  async sendMessage(data: SendMessageRequest): Promise<void> {
    await chatClient.post("/messages/send", data);
  },

  async getUsers(): Promise<User[]> {
    const res = await chatClient.get<User[]>("/users");
    return res.data;
  },

  async updateChat(chatId: number, data: UpdateChatRequest): Promise<void> {
    await chatClient.patch(`/chat/${chatId}`, data);
  },

  async removeMember(chatId: number, memberId: number, requestUserId: number): Promise<void> {
    await chatClient.delete(`/chat/${chatId}/members/${memberId}`, {
      params: { requestUserId },
    });
  },
};
