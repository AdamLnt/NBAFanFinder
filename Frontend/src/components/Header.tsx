import { palette } from "../styles/palette";
import logoSvg from "../assets/logo.svg";
import userPng from "../assets/user.png";

interface HeaderProps {
  onNavigateToLogin?: () => void;
}

export const Header = ({ onNavigateToLogin }: HeaderProps) => (
  <header
    style={{
      background: palette.navy,
      padding: "14px 32px",
    }}
  >
    {/* Rectangle blanc qui englobe tout */}
    <div
      style={{
        background: palette.white,
        borderRadius: 12,
        padding: "12px 24px",
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
      }}
    >
      {/* Logo à gauche */}
      <img src={logoSvg} alt="NBA Fan Finder" style={{ height: 52 }} />

      {/* Boutons au centre */}
      <div
        style={{
          display: "flex",
          gap: 52,
          alignItems: "center",
          position: "absolute",
          left: "50%",
          transform: "translateX(-50%)",
        }}
      >
        <button
          style={{
            background: "none",
            border: "none",
            color: palette.navy,
            fontSize: 30,
            fontFamily: "'Outfit', sans-serif",
            fontWeight: 600,
            cursor: "pointer",
            padding: "8px 16px",
          }}
        >
          Carte
        </button>
        <button
          style={{
            background: "none",
            border: "none",
            color: palette.navy,
            fontSize: 30,
            fontFamily: "'Outfit', sans-serif",
            fontWeight: 600,
            cursor: "pointer",
            padding: "8px 16px",
          }}
        >
          Chat
        </button>
      </div>

      {/* Icône utilisateur à droite */}
      <img
        src={userPng}
        alt="Compte"
        onClick={onNavigateToLogin}
        style={{
          height: 40,
          width: 40,
          cursor: "pointer",
          borderRadius: "50%",
        }}
      />
    </div>
  </header>
);
