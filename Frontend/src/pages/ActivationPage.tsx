import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Header } from "../components/Header";
import { Footer } from "../components/Footer";
import { Card, Title, SubmitButton } from "../components/FormComponents";
import { palette } from "../styles/palette";
import { apiService } from "../services/apiService";

export const ActivationPage = () => {
  const navigate = useNavigate();
  const { userId } = useParams<{ userId: string }>();
  const [activated, setActivated] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Générer le lien d'activation
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
      // Appeler l'API d'activation
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

      <main
        style={{
          flex: 1,
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          background: palette.bg,
          padding: "48px 16px",
        }}
      >
        <Card>
          <Title>Activation du compte</Title>

          {!activated ? (
            <div style={{ textAlign: "center", fontFamily: "'Outfit', sans-serif" }}>
              <p style={{ color: "#4B5563", fontSize: 15, lineHeight: 1.6, marginBottom: 20 }}>
                Merci pour votre inscription ! Cliquez sur le lien ci‑dessous pour activer votre compte.
              </p>

              {/* Lien d'activation affiché */}
              <div
                style={{
                  background: "#F3F4F6",
                  padding: "12px 16px",
                  borderRadius: 8,
                  marginBottom: 20,
                  fontSize: 13,
                  color: "#6B7280",
                  wordBreak: "break-all",
                  fontFamily: "monospace",
                }}
              >
                {activationLink}
              </div>

              {/* Message d'erreur */}
              {error && (
                <div
                  style={{
                    background: "#FEE2E2",
                    color: "#991B1B",
                    padding: "12px 16px",
                    borderRadius: 8,
                    fontSize: 14,
                    marginBottom: 16,
                  }}
                >
                  {error}
                </div>
              )}

              <a
                href={activationLink}
                onClick={handleActivate}
                style={{
                  display: "inline-block",
                  color: palette.white,
                  background: palette.navy,
                  fontWeight: 600,
                  fontSize: 16,
                  padding: "12px 32px",
                  borderRadius: 8,
                  textDecoration: "none",
                  cursor: loading ? "not-allowed" : "pointer",
                  opacity: loading ? 0.7 : 1,
                  transition: "all .2s",
                }}
                onMouseEnter={(e) => {
                  if (!loading) e.currentTarget.style.background = palette.navyDark;
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.background = palette.navy;
                }}
              >
                {loading ? "Activation en cours..." : "Activer mon compte →"}
              </a>
            </div>
          ) : (
            <div style={{ textAlign: "center", fontFamily: "'Outfit', sans-serif" }}>
              <div
                style={{
                  width: 64,
                  height: 64,
                  borderRadius: "50%",
                  background: "#D1FAE5",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  margin: "0 auto 20px",
                  fontSize: 30,
                }}
              >
                ✓
              </div>
              <p style={{ color: "#065F46", fontSize: 16, fontWeight: 600, marginBottom: 8 }}>
                Compte activé avec succès !
              </p>
              <p style={{ color: "#6B7280", fontSize: 14, marginBottom: 24 }}>
                Vous pouvez maintenant vous connecter.
              </p>
              <SubmitButton onClick={() => navigate("/login")}>Se connecter</SubmitButton>
            </div>
          )}
        </Card>
      </main>

      <Footer />
    </>
  );
};
