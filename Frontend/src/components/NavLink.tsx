import "../styles/NavLink.css";

interface NavLinkProps {
  label: string;
  onClick: () => void;
  active?: boolean;
}

export const NavLink = ({ label, onClick, active }: NavLinkProps) => (
  <button
    onClick={onClick}
    className={active ? "nav-link nav-link--active" : "nav-link"}
  >
    {label}
  </button>
);
