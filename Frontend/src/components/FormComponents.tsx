import type { ReactNode, ChangeEvent } from "react";
import "../styles/FormComponents.css";

interface CardProps {
  children: ReactNode;
}

export const Card = ({ children }: CardProps) => (
  <div className="card">{children}</div>
);

interface TitleProps {
  children: ReactNode;
}

export const Title = ({ children }: TitleProps) => (
  <h2 className="card__title">{children}</h2>
);

interface LabelProps {
  children: ReactNode;
}

export const Label = ({ children }: LabelProps) => (
  <label className="form-label">{children}</label>
);

interface FieldProps {
  label: string;
  type?: string;
  placeholder?: string;
  value: string;
  onChange: (e: ChangeEvent<HTMLInputElement>) => void;
  name: string;
}

export const Field = ({ label, type = "text", placeholder, value, onChange, name }: FieldProps) => (
  <div className="form-field">
    <Label>{label}</Label>
    <input
      type={type}
      placeholder={placeholder}
      value={value}
      onChange={onChange}
      name={name}
      className="form-input"
    />
  </div>
);

interface SubmitButtonProps {
  children: ReactNode;
  onClick: () => void;
}

export const SubmitButton = ({ children, onClick }: SubmitButtonProps) => (
  <button onClick={onClick} className="submit-btn">
    {children}
  </button>
);

interface LinkTextProps {
  children: ReactNode;
  onClick: () => void;
}

export const LinkText = ({ children, onClick }: LinkTextProps) => (
  <p className="link-text">
    {children}{" "}
    <span onClick={onClick} className="link-text__action">
      Cliquez ici
    </span>
  </p>
);
