// URL de base de l'API. En dev : laisser vide pour pointer sur le proxy Vite/nginx,
// ou mettre http://localhost:8080/api. En prod : injecte VITE_API_URL au build.
export const API_BASE_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080/api";
