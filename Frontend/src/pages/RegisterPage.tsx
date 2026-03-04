import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Header } from "../components/Header";
import { Footer } from "../components/Footer";
import { Card, Title, Field, SubmitButton, LinkText, Label } from "../components/FormComponents";
import { apiService } from "../services/apiService";
import "../styles/FormComponents.css";
import "../styles/shared.css";
import "../styles/RegisterPage.css";

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
    setError(null);
  };

  const handleSubmit = async () => {
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
      const response = await apiService.register({
        nom: form.nom,
        prenom: form.prenom,
        email: form.email,
        password: form.password,
        dateNaissance: form.dateNaissance || undefined,
      });
      navigate(`/activation/${response.id}`);
    } catch (err: any) {
      setError(err.message || "Erreur lors de l'inscription");
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Header />

      <main className="register-main">
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
          <div className="form-field">
            <Label>Date de naissance</Label>
            <input
              type="date"
              name="dateNaissance"
              value={form.dateNaissance}
              onChange={handleChange}
              className="form-input"
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

          {error && <div className="error-box">{error}</div>}

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
