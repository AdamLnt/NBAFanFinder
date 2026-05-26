import { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { Header } from "../components/Header";
import { Footer } from "../components/Footer";
import { Card, Title, Field, SubmitButton, LinkText, Label } from "../components/FormComponents";
import { apiService } from "../services/apiService";
import { teamService } from "../services/teamService";
import { geocodingService } from "../services/geocodingService";
import type { Team } from "../types/team";
import type { GeocodingResult } from "../types/map";
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
  const [addressQuery, setAddressQuery] = useState("");
  const [addressSuggestions, setAddressSuggestions] = useState<GeocodingResult[]>([]);
  const [selectedAddress, setSelectedAddress] = useState<GeocodingResult | null>(null);
  const [teams, setTeams] = useState<Team[]>([]);
  const [selectedTeamIds, setSelectedTeamIds] = useState<number[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const searchTimeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  useEffect(() => {
    teamService.getAll().then(setTeams).catch(() => setTeams([]));
  }, []);

  useEffect(() => {
    if (searchTimeoutRef.current) {
      clearTimeout(searchTimeoutRef.current);
    }
    if (!addressQuery || addressQuery === selectedAddress?.displayName) {
      setAddressSuggestions([]);
      return;
    }
    searchTimeoutRef.current = setTimeout(async () => {
      const results = await geocodingService.search(addressQuery);
      setAddressSuggestions(results);
    }, 400);
  }, [addressQuery, selectedAddress]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setError(null);
  };

  const handleAddressInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    setAddressQuery(e.target.value);
    setSelectedAddress(null);
    setError(null);
  };

  const handleAddressPick = (result: GeocodingResult) => {
    setSelectedAddress(result);
    setAddressQuery(result.displayName);
    setAddressSuggestions([]);
  };

  const toggleTeam = (teamId: number) => {
    setSelectedTeamIds((prev) =>
      prev.includes(teamId) ? prev.filter((id) => id !== teamId) : [...prev, teamId]
    );
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
    if (!selectedAddress) {
      setError("Veuillez sélectionner une adresse dans la liste de suggestions");
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
        adresse: {
          numero: selectedAddress.numero || "0",
          rue: selectedAddress.rue || selectedAddress.ville,
          ville: selectedAddress.ville,
          code_postal: selectedAddress.codePostal || "00000",
          pays: selectedAddress.pays,
          latitude: selectedAddress.latitude,
          longitude: selectedAddress.longitude,
        },
        equipes_supportees_ids: selectedTeamIds,
      });
      navigate(`/activation/${response.id}`);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Erreur lors de l'inscription");
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
          <Field label="Nom" placeholder="Entrez votre nom" name="nom" value={form.nom} onChange={handleChange} />
          <Field label="Prénom" placeholder="Entrez votre prénom" name="prenom" value={form.prenom} onChange={handleChange} />
          <Field label="Email" type="email" placeholder="exemple@email.com" name="email" value={form.email} onChange={handleChange} />
          <div className="form-field">
            <Label>Date de naissance</Label>
            <input type="date" name="dateNaissance" value={form.dateNaissance} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-field address-field">
            <Label>Adresse</Label>
            <input
              type="text"
              placeholder="Commencez à taper votre adresse..."
              value={addressQuery}
              onChange={handleAddressInput}
              className="form-input"
              autoComplete="off"
            />
            {addressSuggestions.length > 0 && (
              <ul className="address-suggestions">
                {addressSuggestions.map((s, idx) => (
                  <li key={`${s.latitude}-${s.longitude}-${idx}`}>
                    <button
                      type="button"
                      className="address-suggestion-btn"
                      onClick={() => handleAddressPick(s)}
                    >
                      {s.displayName}
                    </button>
                  </li>
                ))}
              </ul>
            )}
            {selectedAddress && (
              <p className="address-confirmation">
                Adresse sélectionnée : {selectedAddress.ville}, {selectedAddress.pays}
              </p>
            )}
          </div>

          <div className="form-field">
            <Label>Équipes NBA supportées (optionnel)</Label>
            <div className="teams-grid">
              {teams.map((team) => (
                <button
                  type="button"
                  key={team.id}
                  className={`team-chip ${selectedTeamIds.includes(team.id) ? "team-chip--selected" : ""}`}
                  onClick={() => toggleTeam(team.id)}
                >
                  {team.nom}
                </button>
              ))}
            </div>
          </div>

          <Field label="Mot de passe" type="password" placeholder="Créez un mot de passe" name="password" value={form.password} onChange={handleChange} />
          <Field label="Confirmation" type="password" placeholder="Confirmez le mot de passe" name="confirmation" value={form.confirmation} onChange={handleChange} />

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
