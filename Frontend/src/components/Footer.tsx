import { palette } from "../styles/palette";

export const Footer = () => (
  <footer
    style={{
      background: palette.navy,
      display: "flex",
      alignItems: "center",
      justifyContent: "space-between",
      padding: "10px 24px",
      marginTop: "auto",
    }}
  >
    <span
      style={{
        color: palette.white,
        fontSize: 13,
        fontFamily: "'Outfit', sans-serif",
        display: "flex",
        alignItems: "center",
        gap: 6,
        border: "1.5px solid white",
        borderRadius: 20,
        padding: "4px 14px",
      }}
    >
      © Tous droits réservés
    </span>
    <button
      style={{
        background: "none",
        border: "1.5px solid white",
        color: palette.white,
        borderRadius: 20,
        padding: "4px 18px",
        fontSize: 13,
        fontFamily: "'Outfit', sans-serif",
        cursor: "pointer",
      }}
    >
      Me contacter
    </button>
  </footer>
);
