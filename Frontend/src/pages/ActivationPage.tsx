import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Header } from "../components/Header";
import { Footer } from "../components/Footer";
import { Card, Title, SubmitButton } from "../components/FormComponents";
import { apiService } from "../services/apiService";
import { API_BASE_URL } from "../services/apiConfig";
import "../styles/shared.css";
import "../styles/ActivationPage.css";

export const ActivationPage = () => {
  const navigate = useNavigate();
  const { token } = useParams<{ token: string }>();
  const [activated, setActivated] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const activationLink = token
    ? `${API_BASE_URL}/auth/activate/${encodeURIComponent(token)}`
    : "";

  const handleActivate = async (e: React.MouseEvent<HTMLAnchorElement>) => {
    e.preventDefault();

    if (!token) {
      setError("Token d'activation manquant");
      return;
    }

    setLoading(true);
    setError(null);

    try {
      await apiService.activateAccount(token);
      setActivated(true);
    } catch (err: any) {
      setError(err.message || "Erreur lors de l'activation du compte");
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Header />

      <main className="page-main">
        <Card>
          <Title>Activation du compte</Title>

          {!activated ? (
            <div className="activation-pending">
              <p className="activation-pending__intro">
                Merci pour votre inscription ! Cliquez sur le lien ci‑dessous pour activer votre
                compte.
              </p>

              <div className="activation-link-box">{activationLink}</div>

              {error && <div className="error-box">{error}</div>}

              <a
                href={activationLink}
                onClick={handleActivate}
                className={`activation-btn${loading ? " activation-btn--loading" : ""}`}
              >
                {loading ? "Activation en cours..." : "Activer mon compte →"}
              </a>
            </div>
          ) : (
            <div className="activation-success">
              <div className="activation-success__badge">✓</div>
              <p className="activation-success__title">Compte activé avec succès !</p>
              <p className="activation-success__subtitle">Vous pouvez maintenant vous connecter.</p>
              <SubmitButton onClick={() => navigate("/login")}>Se connecter</SubmitButton>
            </div>
          )}
        </Card>
      </main>

      <Footer />
    </>
  );
};
