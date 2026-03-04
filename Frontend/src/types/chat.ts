export interface ChatMember {
  id: number;
  nom: string;
  prenom: string;
}

export interface Chat {
  id: number;
  nom: string;
  description: string;
  proprietaires: ChatMember[];
  membres: ChatMember[];
}

// date_envoi peut être un tableau [year, month, day, h, m, s] ou une string ISO selon la config Jackson
export interface Message {
  idMessage: number;
  idChat: number;
  idUser: number;
  texte: string;
  date_envoi: number[] | string;
  nom_utilisateur: string;
  prenom_utilisateur: string;
}

export interface User {
  id: number;
  nom: string;
  prenom: string;
  email: string;
}

export interface CreateChatRequest {
  nom: string;
  description: string;
  userId: number;
  membresIds: number[];
  proprietairesIds?: number[];
}

export interface JoinChatRequest {
  chatId: number;
  userId: number;
}

export interface SendMessageRequest {
  chatId: number;
  userId: number;
  texte: string;
}

export interface UpdateChatRequest {
  nom?: string;
  description?: string;
  requestUserId: number;
}
