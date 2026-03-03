import { useNavigate } from "react-router-dom";
import { authService } from "../services/authService";
import logoSvg from "../assets/logo.svg";
import userPng from "../assets/user.png";
import { MapIcon } from "./icons/MapIcon";
import { ChatIcon } from "./icons/ChatIcon";
import "../styles/Header.css";

interface HeaderProps {
  onNavigateToLogin?: () => void;
}

export const Header = ({ onNavigateToLogin }: HeaderProps) => {
  const navigate = useNavigate();

  return (
    <header className="header">
      <div className="header__left">
        <img src={logoSvg} alt="NBA Fan Finder" className="header__logo" />

        <button onClick={() => navigate("/map")} className="header__nav-btn">
          <MapIcon />
        </button>

        <button onClick={() => navigate("/chat")} className="header__nav-btn">
          <ChatIcon />
        </button>
      </div>

      <div className="header__right">
        <img
          src={userPng}
          alt="Compte"
          onClick={onNavigateToLogin}
          className="header__user-avatar"
        />
        <button
          onClick={() => { authService.logout(); navigate("/login"); }}
          className="header__logout-btn"
        >
          Déconnexion
        </button>
      </div>
    </header>
  );
};
