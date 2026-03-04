import { useState, useRef, useEffect } from "react";
import type { Chat, ChatMember, Message, User } from "../../types/chat";

interface Props {
  chat: Chat | null;
  messages: Message[];
  currentUserId: number;
  onSendMessage: (text: string) => Promise<void>;
  loading: boolean;
  users: User[];
  onStartChat: (user: User) => Promise<void>;
}

function parseDate(dateEnvoi: number[] | string): Date {
  if (Array.isArray(dateEnvoi)) {
    const [year, month, day, hour = 0, minute = 0, second = 0] = dateEnvoi;
    return new Date(year, month - 1, day, hour, minute, second);
  }
  return new Date(dateEnvoi);
}

function formatTime(dateEnvoi: number[] | string): string {
  return parseDate(dateEnvoi).toLocaleTimeString("fr-FR", { hour: "2-digit", minute: "2-digit" });
}

function MemberItem({ member, isOwner }: { member: ChatMember; isOwner: boolean }) {
  return (
    <div className="members-panel__item">
      <div className="members-panel__avatar">{member.prenom.charAt(0).toUpperCase()}</div>
      <span className="members-panel__name">{member.prenom} {member.nom}</span>
      {isOwner && <span className="members-panel__badge">Admin</span>}
    </div>
  );
}

export const ChatWindow = ({ chat, messages, currentUserId, onSendMessage, loading, users, onStartChat }: Props) => {
  const [text, setText] = useState("");
  const [sending, setSending] = useState(false);
  const [showMembers, setShowMembers] = useState(false);
  const [startingChat, setStartingChat] = useState<number | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  useEffect(() => {
    setShowMembers(false);
  }, [chat?.id]);

  const handleSend = async () => {
    if (!text.trim() || sending) return;
    setSending(true);
    try {
      await onSendMessage(text.trim());
      setText("");
    } finally {
      setSending(false);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  const handleStartChat = async (user: User) => {
    setStartingChat(user.id);
    try {
      await onStartChat(user);
    } finally {
      setStartingChat(null);
    }
  };

  if (!chat) {
    return (
      <div className="chat-window chat-window--empty">
        <div className="users-grid">
          <div className="users-grid__header">
            <h2 className="users-grid__title">Démarrer une conversation</h2>
            <p className="users-grid__subtitle">Cliquez sur un utilisateur pour créer un chat direct</p>
          </div>
          {users.length === 0 ? (
            <p className="users-grid__empty">Aucun autre utilisateur disponible</p>
          ) : (
            <div className="users-grid__cards">
              {users.map((u) => (
                <button
                  key={u.id}
                  className="user-card"
                  onClick={() => handleStartChat(u)}
                  disabled={startingChat === u.id}
                >
                  <div className="user-card__avatar">{u.prenom.charAt(0).toUpperCase()}</div>
                  <span className="user-card__name">{u.prenom} {u.nom}</span>
                  <span className="user-card__email">{u.email}</span>
                </button>
              ))}
            </div>
          )}
        </div>
      </div>
    );
  }

  const totalMembers = (chat.proprietaires?.length ?? 0) + (chat.membres?.length ?? 0);

  return (
    <div className="chat-window">
      <div className="chat-window__header">
        <div className="chat-window__header-avatar">{chat.nom.charAt(0).toUpperCase()}</div>
        <div className="chat-window__header-info">
          <h3 className="chat-window__header-name">{chat.nom}</h3>
          <p className="chat-window__header-desc">{chat.description}</p>
        </div>
        <button
          className={`chat-window__members-btn${showMembers ? " chat-window__members-btn--active" : ""}`}
          onClick={() => setShowMembers((v) => !v)}
          title="Voir les membres"
        >
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
            <circle cx="9" cy="7" r="4" />
            <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
            <path d="M16 3.13a4 4 0 0 1 0 7.75" />
          </svg>
          <span>{totalMembers}</span>
        </button>
      </div>

      <div className="chat-window__body">
        <div className="chat-window__messages">
          {loading ? (
            <p className="chat-window__status">Chargement des messages...</p>
          ) : messages.length === 0 ? (
            <p className="chat-window__status">Aucun message. Dites bonjour !</p>
          ) : (
            messages.map((msg) => {
              const isMe = msg.idUser === currentUserId;
              return (
                <div
                  key={msg.idMessage}
                  className={`chat-message${isMe ? " chat-message--me" : " chat-message--other"}`}
                >
                  {!isMe && (
                    <span className="chat-message__author">
                      {msg.prenom_utilisateur} {msg.nom_utilisateur}
                    </span>
                  )}
                  <div className="chat-message__bubble">{msg.texte}</div>
                  <span className="chat-message__time">{formatTime(msg.date_envoi)}</span>
                </div>
              );
            })
          )}
          <div ref={messagesEndRef} />
        </div>

        {showMembers && (
          <aside className="members-panel">
            <h4 className="members-panel__title">Membres</h4>

            {chat.proprietaires?.length > 0 && (
              <div className="members-panel__section">
                <span className="members-panel__section-label">Administrateurs</span>
                {chat.proprietaires.map((m) => (
                  <MemberItem key={m.id} member={m} isOwner={true} />
                ))}
              </div>
            )}

            {chat.membres?.length > 0 && (
              <div className="members-panel__section">
                <span className="members-panel__section-label">Membres</span>
                {chat.membres.map((m) => (
                  <MemberItem key={m.id} member={m} isOwner={false} />
                ))}
              </div>
            )}

            {totalMembers === 0 && (
              <p className="members-panel__empty">Aucun membre</p>
            )}
          </aside>
        )}
      </div>

      <div className="chat-window__input-area">
        <input
          type="text"
          className="chat-window__input"
          placeholder="Écrivez un message..."
          value={text}
          onChange={(e) => setText(e.target.value)}
          onKeyDown={handleKeyDown}
          disabled={sending}
        />
        <button
          className="chat-window__send-btn"
          onClick={handleSend}
          disabled={!text.trim() || sending}
          title="Envoyer"
        >
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <line x1="22" y1="2" x2="11" y2="13" />
            <polygon points="22 2 15 22 11 13 2 9 22 2" />
          </svg>
        </button>
      </div>
    </div>
  );
};
