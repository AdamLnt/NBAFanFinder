import { useNavigate } from "react-router-dom";
import { Header } from "../components/Header";
import { Footer } from "../components/Footer";
import { Card, Title, SubmitButton } from "../components/FormComponents";
import { palette } from "../styles/palette";
import { authService } from "../services/authService";
import { useEffect, useState } from "react";

export const HomePage = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState<{ email: string; nom: string; prenom: string } | null>(null);

  useEffect(() => {
    // Vérifier si l'utilisateur est authentifié
    if (!authService.isAuthenticated()) {
      navigate("/login");
      return;
    }

    // Récupérer les informations utilisateur
    const userData = authService.getUser();
    setUser(userData);
  }, [navigate]);

  const handleLogout = () => {
    authService.logout();
    navigate("/login");
  };

  if (!user) {
    return null; // Ou un loader
  }

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
          <Title>Bienvenue !</Title>

          <div style={{ textAlign: "center", fontFamily: "'Outfit', sans-serif", marginBottom: 24 }}>
            <div
              style={{
                width: 80,
                height: 80,
                borderRadius: "50%",
                background: "#D1FAE5",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                margin: "0 auto 20px",
                fontSize: 40,
              }}
            >
              ✓
            </div>

            <p style={{ color: "#065F46", fontSize: 18, fontWeight: 600, marginBottom: 8 }}>
              Vous êtes connecté !
            </p>

            <div style={{ color: palette.navy, fontSize: 15, marginTop: 16, lineHeight: 1.6 }}>
              <p style={{ marginBottom: 4 }}>
                <strong>Nom :</strong> {user.nom}
              </p>
              <p style={{ marginBottom: 4 }}>
                <strong>Prénom :</strong> {user.prenom}
              </p>
              <p style={{ marginBottom: 4 }}>
                <strong>Email :</strong> {user.email}
              </p>
            </div>
          </div>

          <SubmitButton onClick={handleLogout}>Se déconnecter</SubmitButton>
        </Card>
      </main>

      <Footer />
    </>
  );
};
