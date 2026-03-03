import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Header } from "../components/Header";
import { Footer } from "../components/Footer";
import { Card, Title, SubmitButton } from "../components/FormComponents";
import { apiService } from "../services/apiService";
import "../styles/shared.css";
import "../styles/ActivationPage.css";

export const ActivationPage = () => {
  const navigate = useNavigate();
  const { userId } = useParams<{ userId: string }>();
  const [activated, setActivated] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const activationLink = `http://localhost:8080/api/auth/activate/${userId}`;

  const handleActivate = async (e: React.MouseEvent<HTMLAnchorElement>) => {
    e.preventDefault();

    if (!userId) {
      setError("ID utilisateur manquant");
      return;
    }

    setLoading(true);
    setError(null);

    try {
      await apiService.activateAccount(parseInt(userId));
      setActivated(true);
    } catch (err: any) {
      setError(err.message || "Erreur lors de l'activation du compte");
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Header onNavigateToLogin={() => navigate("/login")} />

      <main className="page-main">
        <Card>
          <Title>Activation du compte</Title>

          {!activated ? (
            <div className="activation-pending">
              <p className="activation-pending__intro">
                Merci pour votre inscription ! Cliquez sur le lien ci‑dessous pour activer votre compte.
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
