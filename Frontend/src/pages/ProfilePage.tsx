import { Header } from "../components/Header";
import { authService } from "../services/authService";
import userPng from "../assets/user.png";
import "../styles/ProfilePage.css";

export const ProfilePage = () => {
  const user = authService.getUser();

  return (
    <>
      <Header />
      <main className="profile-page">
        <div className="profile-card">
          <img src={userPng} alt="Avatar" className="profile-avatar" />
          {user && (
            <p className="profile-name">{user.prenom} {user.nom}</p>
          )}
          <div className="profile-info">
            <div className="profile-info-row">
              <span className="profile-info-label">Prénom</span>
              <span className="profile-info-value">{user?.prenom ?? "—"}</span>
            </div>
            <div className="profile-info-row">
              <span className="profile-info-label">Nom</span>
              <span className="profile-info-value">{user?.nom ?? "—"}</span>
            </div>
            <div className="profile-info-row">
              <span className="profile-info-label">Email</span>
              <span className="profile-info-value">{user?.email ?? "—"}</span>
            </div>
          </div>
        </div>
      </main>
    </>
  );
};
