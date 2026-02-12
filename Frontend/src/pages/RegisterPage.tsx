import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Header } from "../components/Header";
import { Footer } from "../components/Footer";
import { Card, Title, Field, SubmitButton, LinkText, Label } from "../components/FormComponents";
import { palette } from "../styles/palette";
import { apiService } from "../services/apiService";

const inputStyle = {
  width: "100%",
  padding: "12px 14px",
  border: `1.5px solid ${palette.border}`,
  borderRadius: 8,
  fontSize: 15,
  fontFamily: "'Outfit', sans-serif",
  outline: "none",
  boxSizing: "border-box" as const,
  color: palette.text,
  transition: "border-color .2s",
};

export const RegisterPage = () => {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    nom: "",
    prenom: "",
    email: "",
    dateNaissance: "",
    password: "",
    confirmation: "",
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setError(null); // Effacer l'erreur lors de la saisie
  };

  const handleSubmit = async () => {
    // Validation basique
    if (!form.nom || !form.prenom || !form.email || !form.password || !form.confirmation) {
      setError("Veuillez remplir tous les champs obligatoires");
      return;
    }

    if (form.password !== form.confirmation) {
      setError("Les mots de passe ne correspondent pas !");
      return;
    }

    setLoading(true);
    setError(null);

    try {
      // Appel à l'API d'inscription
      const response = await apiService.register({
        nom: form.nom,
        prenom: form.prenom,
        email: form.email,
        password: form.password,
        dateNaissance: form.dateNaissance || undefined,
      });

      // Le compte est créé mais pas encore activé
      // Rediriger vers la page d'activation avec l'ID utilisateur
      navigate(`/activation/${response.id}`);
    } catch (err: any) {
      setError(err.message || "Erreur lors de l'inscription");
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
          padding: "36px 16px",
        }}
      >
        <Card>
          <Title>Inscription</Title>
          <Field
            label="Nom"
            placeholder="Entrez votre nom"
            name="nom"
            value={form.nom}
            onChange={handleChange}
          />
          <Field
            label="Prénom"
            placeholder="Entrez votre prénom"
            name="prenom"
            value={form.prenom}
            onChange={handleChange}
          />
          <Field
            label="Email"
            type="email"
            placeholder="exemple@email.com"
            name="email"
            value={form.email}
            onChange={handleChange}
          />
          <div style={{ marginBottom: 18 }}>
            <Label>Date de naissance</Label>
            <input
              type="date"
              name="dateNaissance"
              value={form.dateNaissance}
              onChange={handleChange}
              style={inputStyle}
              onFocus={(e) => (e.currentTarget.style.borderColor = palette.navy)}
              onBlur={(e) => (e.currentTarget.style.borderColor = palette.border)}
            />
          </div>
          <Field
            label="Mot de passe"
            type="password"
            placeholder="Créez un mot de passe"
            name="password"
            value={form.password}
            onChange={handleChange}
          />
          <Field
            label="Confirmation"
            type="password"
            placeholder="Confirmez le mot de passe"
            name="confirmation"
            value={form.confirmation}
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
            {loading ? "Inscription en cours..." : "S'inscrire"}
          </SubmitButton>
          <LinkText onClick={() => navigate("/login")}>Déjà un compte ?</LinkText>
        </Card>
      </main>

      <Footer />
    </>
  );
};
