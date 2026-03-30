import type { Chat } from "../../types/chat";

interface Props {
  chats: Chat[];
  selectedChatId: number | null;
  onSelectChat: (chat: Chat) => void;
  onOpenModal: () => void;
  onNewConversation: () => void;
  loading: boolean;
}

export const ChatSidebar = ({ chats, selectedChatId, onSelectChat, onOpenModal, onNewConversation, loading }: Props) => {
  return (
    <aside className="chat-sidebar">
      <div className="chat-sidebar__header">
        <h2 className="chat-sidebar__title">Mes Chats</h2>
        <div className="chat-sidebar__actions">
          <button className="chat-sidebar__compose-btn" onClick={onNewConversation} title="Nouvelle conversation directe">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M12 20h9" />
              <path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z" />
            </svg>
          </button>
          <button className="chat-sidebar__add-btn" onClick={onOpenModal} title="Créer ou rejoindre un chat">
            +
          </button>
        </div>
      </div>
      <div className="chat-sidebar__list">
        {loading ? (
          <p className="chat-sidebar__empty">Chargement...</p>
        ) : chats.length === 0 ? (
          <p className="chat-sidebar__empty">Aucun chat. Créez-en un !</p>
        ) : (
          chats.map((chat) => (
            <button
              key={chat.id}
              className={`chat-sidebar__item${selectedChatId === chat.id ? " chat-sidebar__item--active" : ""}`}
              onClick={() => onSelectChat(chat)}
            >
              <span className="chat-sidebar__item-avatar">
                {chat.nom.charAt(0).toUpperCase()}
              </span>
              <div className="chat-sidebar__item-info">
                <span className="chat-sidebar__item-name">{chat.nom}</span>
                <span className="chat-sidebar__item-desc">{chat.description}</span>
              </div>
            </button>
          ))
        )}
      </div>
    </aside>
  );
};
