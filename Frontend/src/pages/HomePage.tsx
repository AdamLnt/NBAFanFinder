import { useNavigate } from "react-router-dom";
import { Header } from "../components/Header";
import { Footer } from "../components/Footer";
import { Card, Title, SubmitButton } from "../components/FormComponents";
import { authService } from "../services/authService";
import { useEffect, useState } from "react";
import "../styles/shared.css";
import "../styles/HomePage.css";

export const HomePage = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState<{ email: string; nom: string; prenom: string } | null>(null);

  useEffect(() => {
    if (!authService.isAuthenticated()) {
      navigate("/login");
      return;
    }
    const userData = authService.getUser();
    setUser(userData);
  }, [navigate]);

  const handleLogout = () => {
    authService.logout();
    navigate("/login");
  };

  if (!user) {
    return null;
  }

  return (
    <>
      <Header onNavigateToLogin={() => navigate("/login")} />

      <main className="page-main">
        <Card>
          <Title>Bienvenue !</Title>

          <div className="home-card-content">
            <div className="home-success-badge">✓</div>

            <p className="home-connected-msg">Vous êtes connecté !</p>

            <div className="home-user-info">
              <p><strong>Nom :</strong> {user.nom}</p>
              <p><strong>Prénom :</strong> {user.prenom}</p>
              <p><strong>Email :</strong> {user.email}</p>
            </div>
          </div>

          <SubmitButton onClick={handleLogout}>Se déconnecter</SubmitButton>
        </Card>
      </main>

      <Footer />
    </>
  );
};
