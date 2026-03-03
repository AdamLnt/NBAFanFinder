import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Header } from "../components/Header";
import { Footer } from "../components/Footer";
import { Card, Title, Field, SubmitButton, LinkText } from "../components/FormComponents";
import { apiService } from "../services/apiService";
import { authService } from "../services/authService";
import "../styles/shared.css";

export const LoginPage = () => {
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: "", password: "" });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setError(null);
  };

  const handleSubmit = async () => {
    if (!form.email || !form.password) {
      setError("Veuillez remplir tous les champs");
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await apiService.login(form);
      authService.setAuth(response);
      navigate("/map");
    } catch (err: any) {
      setError(err.message || "Erreur lors de la connexion");
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Header onNavigateToLogin={() => navigate("/login")} />

      <main className="page-main">
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

          {error && <div className="error-box">{error}</div>}

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
