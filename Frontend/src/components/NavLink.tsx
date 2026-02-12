import { palette } from "../styles/palette";

interface NavLinkProps {
  label: string;
  onClick: () => void;
  active?: boolean;
}

export const NavLink = ({ label, onClick, active }: NavLinkProps) => (
  <button
    onClick={onClick}
    style={{
      background: "none",
      border: "none",
      color: palette.white,
      fontSize: 18,
      fontFamily: "'Outfit', sans-serif",
      fontWeight: active ? 700 : 500,
      cursor: "pointer",
      textDecoration: active ? "underline" : "none",
      textUnderlineOffset: 4,
    }}
  >
    {label}
  </button>
);
