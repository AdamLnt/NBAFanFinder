import { useState, useEffect } from "react";
import type { Chat } from "../../types/chat";

interface Props {
  chat: Chat;
  onClose: () => void;
  onUpdateChat: (nom: string, description: string) => Promise<void>;
  onRemoveMember: (memberId: number) => Promise<void>;
}

export const EditChatModal = ({ chat, onClose, onUpdateChat, onRemoveMember }: Props) => {
  const [nom, setNom] = useState(chat.nom);
  const [description, setDescription] = useState(chat.description ?? "");
  const [savingInfo, setSavingInfo] = useState(false);
  const [removingId, setRemovingId] = useState<number | null>(null);
  const [error, setError] = useState("");

  useEffect(() => {
    setNom(chat.nom);
    setDescription(chat.description ?? "");
  }, [chat.nom, chat.description]);

  const infoChanged =
    nom.trim() !== chat.nom || description.trim() !== (chat.description ?? "");

  const handleSave = async () => {
    if (!nom.trim() || !infoChanged) return;
    setSavingInfo(true);
    setError("");
    try {
      await onUpdateChat(nom.trim(), description.trim());
    } catch {
      setError("Erreur lors de la mise à jour.");
    } finally {
      setSavingInfo(false);
    }
  };

  const handleRemove = async (memberId: number) => {
    setRemovingId(memberId);
    setError("");
    try {
      await onRemoveMember(memberId);
    } catch {
      setError("Erreur lors de la suppression du membre.");
    } finally {
      setRemovingId(null);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal__header">
          <h3 className="modal__title">Paramètres du chat</h3>
          <button className="modal__close" onClick={onClose}>✕</button>
        </div>

        {error && <p className="modal__error">{error}</p>}

        <div className="modal__body">
          <div className="modal__field">
            <label className="modal__label">Nom du chat</label>
            <input
              type="text"
              className="modal__input"
              value={nom}
              onChange={(e) => setNom(e.target.value)}
            />
          </div>
          <div className="modal__field">
            <label className="modal__label">Description</label>
            <input
              type="text"
              className="modal__input"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />
          </div>
          <button
            className="modal__btn"
            onClick={handleSave}
            disabled={!nom.trim() || !infoChanged || savingInfo}
          >
            {savingInfo ? "Enregistrement..." : "Enregistrer les modifications"}
          </button>

          {chat.membres.length > 0 && (
            <div className="modal__field">
              <label className="modal__label">Membres</label>
              <div className="user-picker">
                {chat.membres.map((m) => (
                  <div key={m.id} className="modal__member-item">
                    <div className="user-picker__avatar">{m.prenom.charAt(0).toUpperCase()}</div>
                    <div className="user-picker__info">
                      <span className="user-picker__name">{m.prenom} {m.nom}</span>
                    </div>
                    <button
                      className="member-remove-btn"
                      onClick={() => handleRemove(m.id)}
                      disabled={removingId === m.id}
                      title="Retirer ce membre"
                    >
                      {removingId === m.id ? "..." : "✕"}
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
