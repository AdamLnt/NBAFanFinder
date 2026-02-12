// Request DTOs
export interface RegisterRequest {
  nom: string;
  prenom: string;
  email: string;
  password: string;
  dateNaissance?: string; // Format: YYYY-MM-DD
}

export interface LoginRequest {
  email: string;
  password: string;
}

// Response DTOs
export interface AuthResponse {
  id: number;
  token: string | null; // Peut être null si le compte n'est pas activé
  email: string;
  nom: string;
  prenom: string;
}

export interface ErrorResponse {
  error: string;
}
