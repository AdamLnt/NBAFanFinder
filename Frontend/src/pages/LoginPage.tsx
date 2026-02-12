import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Header } from "../components/Header";
import { Footer } from "../components/Footer";
import { Card, Title, Field, SubmitButton, LinkText } from "../components/FormComponents";
import { palette } from "../styles/palette";
import { apiService } from "../services/apiService";
import { authService } from "../services/authService";

export const LoginPage = () => {
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: "", password: "" });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setError(null); // Effacer l'erreur lors de la saisie
  };

  const handleSubmit = async () => {
    // Validation basique
    if (!form.email || !form.password) {
      setError("Veuillez remplir tous les champs");
      return;
    }

    setLoading(true);
    setError(null);

    try {
      // Appel à l'API de connexion
      const response = await apiService.login(form);

      // Stocker le token et les informations utilisateur
      authService.setAuth(response);

      // Rediriger vers la page d'accueil
      navigate("/home");
    } catch (err: any) {
      setError(err.message || "Erreur lors de la connexion");
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
          <Title>Connexion</Title>
          <Field
            label="Email"
            type="email"
            placeholder="exemple@email.com"
            name="email"
            value={form.email}
            onChange={handleChange}
          />
          <Field
            label="Mot de passe"
            type="password"
            placeholder="Entrez votre mot de passe"
            name="password"
            value={form.password}
            onChange={handleChange}
          />

          {/* Message d'erreur */}
          {error && (
            <div
              style={{
                background: "#FEE2E2",
                color: "#991B1B",
                padding: "12px 16px",
                borderRadius: 8,
                fontSize: 14,
                fontFamily: "'Outfit', sans-serif",
                marginBottom: 16,
                textAlign: "center",
              }}
            >
              {error}
            </div>
          )}

          <SubmitButton onClick={handleSubmit}>
            {loading ? "Connexion en cours..." : "Se connecter"}
          </SubmitButton>
          <LinkText onClick={() => navigate("/register")}>Pas encore de compte ?</LinkText>
        </Card>
      </main>

      <Footer />
    </>
  );
};
