import type { ReactNode, ChangeEvent } from "react";
import { palette } from "../styles/palette";

interface CardProps {
  children: ReactNode;
}

export const Card = ({ children }: CardProps) => (
  <div
    style={{
      background: palette.white,
      borderRadius: 16,
      padding: "36px 40px 40px",
      width: "100%",
      maxWidth: 420,
      boxShadow: "0 4px 24px rgba(27,58,107,0.08)",
    }}
  >
    {children}
  </div>
);

interface TitleProps {
  children: ReactNode;
}

export const Title = ({ children }: TitleProps) => (
  <h2
    style={{
      textAlign: "center",
      color: palette.navy,
      fontFamily: "'Outfit', sans-serif",
      fontWeight: 700,
      fontSize: 26,
      fontStyle: "italic",
      marginBottom: 24,
    }}
  >
    {children}
  </h2>
);

interface LabelProps {
  children: ReactNode;
}

export const Label = ({ children }: LabelProps) => (
  <label
    style={{
      display: "block",
      fontFamily: "'Outfit', sans-serif",
      fontWeight: 600,
      fontSize: 14,
      color: palette.navy,
      marginBottom: 6,
    }}
  >
    {children}
  </label>
);

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

interface FieldProps {
  label: string;
  type?: string;
  placeholder?: string;
  value: string;
  onChange: (e: ChangeEvent<HTMLInputElement>) => void;
  name: string;
}

export const Field = ({ label, type = "text", placeholder, value, onChange, name }: FieldProps) => (
  <div style={{ marginBottom: 18 }}>
    <Label>{label}</Label>
    <input
      type={type}
      placeholder={placeholder}
      value={value}
      onChange={onChange}
      name={name}
      style={inputStyle}
      onFocus={(e) => (e.currentTarget.style.borderColor = palette.navy)}
      onBlur={(e) => (e.currentTarget.style.borderColor = palette.border)}
    />
  </div>
);

interface SubmitButtonProps {
  children: ReactNode;
  onClick: () => void;
}

export const SubmitButton = ({ children, onClick }: SubmitButtonProps) => (
  <button
    onClick={onClick}
    style={{
      width: "100%",
      padding: "14px 0",
      background: palette.navy,
      color: palette.white,
      border: "none",
      borderRadius: 8,
      fontSize: 16,
      fontWeight: 600,
      fontFamily: "'Outfit', sans-serif",
      cursor: "pointer",
      marginTop: 8,
      transition: "background .2s",
    }}
    onMouseEnter={(e) => (e.currentTarget.style.background = palette.navyDark)}
    onMouseLeave={(e) => (e.currentTarget.style.background = palette.navy)}
  >
    {children}
  </button>
);

interface LinkTextProps {
  children: ReactNode;
  onClick: () => void;
}

export const LinkText = ({ children, onClick }: LinkTextProps) => (
  <p
    style={{
      textAlign: "center",
      marginTop: 16,
      fontSize: 13,
      fontFamily: "'Outfit', sans-serif",
      color: "#6B7280",
    }}
  >
    {children}{" "}
    <span
      onClick={onClick}
      style={{ color: palette.navy, fontWeight: 600, cursor: "pointer", textDecoration: "underline" }}
    >
      Cliquez ici
    </span>
  </p>
);
