import { useState, useEffect } from "react";
import { chatApiService } from "../../services/chatApiService";
import type { User } from "../../types/chat";

interface Props {
  currentUserId: number;
  onClose: () => void;
  onCreateChat: (nom: string, description: string, membresIds: number[]) => Promise<void>;
  onJoinChat: (chatId: number) => Promise<void>;
}

export const CreateChatModal = ({ currentUserId, onClose, onCreateChat, onJoinChat }: Props) => {
  const [tab, setTab] = useState<"create" | "join">("create");
  const [nom, setNom] = useState("");
  const [description, setDescription] = useState("");
  const [chatId, setChatId] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const [users, setUsers] = useState<User[]>([]);
  const [search, setSearch] = useState("");
  const [selectedIds, setSelectedIds] = useState<Set<number>>(new Set());

  useEffect(() => {
    chatApiService.getUsers().then((all) =>
      setUsers(all.filter((u) => u.id !== currentUserId))
    );
  }, [currentUserId]);

  const filtered = users.filter((u) => {
    const q = search.toLowerCase();
    return (
      u.prenom.toLowerCase().includes(q) ||
      u.nom.toLowerCase().includes(q) ||
      u.email.toLowerCase().includes(q)
    );
  });

  const toggleUser = (id: number) => {
    setSelectedIds((prev) => {
      const next = new Set(prev);
      next.has(id) ? next.delete(id) : next.add(id);
      return next;
    });
  };

  const handleCreate = async () => {
    if (!nom.trim()) return;
    setLoading(true);
    setError("");
    try {
      await onCreateChat(nom.trim(), description.trim(), Array.from(selectedIds));
    } catch {
      setError("Erreur lors de la création du chat.");
    } finally {
      setLoading(false);
    }
  };

  const handleJoin = async () => {
    const id = parseInt(chatId);
    if (isNaN(id)) return;
    setLoading(true);
    setError("");
    try {
      await onJoinChat(id);
    } catch {
      setError("Chat introuvable ou erreur de connexion.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal__header">
          <h3 className="modal__title">
            {tab === "create" ? "Créer un chat" : "Rejoindre un chat"}
          </h3>
          <button className="modal__close" onClick={onClose}>✕</button>
        </div>

        <div className="modal__tabs">
          <button
            className={`modal__tab${tab === "create" ? " modal__tab--active" : ""}`}
            onClick={() => { setTab("create"); setError(""); }}
          >
            Créer
          </button>
          <button
            className={`modal__tab${tab === "join" ? " modal__tab--active" : ""}`}
            onClick={() => { setTab("join"); setError(""); }}
          >
            Rejoindre
          </button>
        </div>

        {error && <p className="modal__error">{error}</p>}

        {tab === "create" ? (
          <div className="modal__body">
            <div className="modal__field">
              <label className="modal__label">Nom du chat</label>
              <input
                type="text"
                className="modal__input"
                placeholder="Ex: Fans des Lakers"
                value={nom}
                onChange={(e) => setNom(e.target.value)}
              />
            </div>
            <div className="modal__field">
              <label className="modal__label">Description</label>
              <input
                type="text"
                className="modal__input"
                placeholder="Description (optionnel)"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
              />
            </div>

            <div className="modal__field">
              <label className="modal__label">
                Inviter des membres
                {selectedIds.size > 0 && (
                  <span className="modal__label-count"> · {selectedIds.size} sélectionné{selectedIds.size > 1 ? "s" : ""}</span>
                )}
              </label>
              <input
                type="text"
                className="modal__input"
                placeholder="Rechercher par nom ou email..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
              <div className="user-picker">
                {filtered.length === 0 ? (
                  <p className="user-picker__empty">Aucun utilisateur trouvé</p>
                ) : (
                  filtered.map((u) => {
                    const selected = selectedIds.has(u.id);
                    return (
                      <button
                        key={u.id}
                        type="button"
                        className={`user-picker__item${selected ? " user-picker__item--selected" : ""}`}
                        onClick={() => toggleUser(u.id)}
                      >
                        <div className="user-picker__avatar">
                          {u.prenom.charAt(0).toUpperCase()}
                        </div>
                        <div className="user-picker__info">
                          <span className="user-picker__name">{u.prenom} {u.nom}</span>
                          <span className="user-picker__email">{u.email}</span>
                        </div>
                        {selected && (
                          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                            <polyline points="20 6 9 17 4 12" />
                          </svg>
                        )}
                      </button>
                    );
                  })
                )}
              </div>
            </div>

            <button
              className="modal__btn"
              onClick={handleCreate}
              disabled={!nom.trim() || loading}
            >
              {loading ? "Création..." : "Créer le chat"}
            </button>
          </div>
        ) : (
          <div className="modal__body">
            <div className="modal__field">
              <label className="modal__label">ID du chat</label>
              <input
                type="number"
                className="modal__input"
                placeholder="Entrez l'ID du chat"
                value={chatId}
                onChange={(e) => setChatId(e.target.value)}
              />
            </div>
            <button
              className="modal__btn"
              onClick={handleJoin}
              disabled={!chatId || loading}
            >
              {loading ? "Connexion..." : "Rejoindre le chat"}
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
