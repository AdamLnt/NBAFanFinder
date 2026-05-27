// URL de base de l'API. Par defaut, chemin relatif "/api" : le navigateur tape sur la
// meme origine que le HTML, ce qui passe le CSP connect-src 'self'. Le proxy (Vite en
// dev, nginx en prod) forward vers le backend. Surchargeable via VITE_API_URL au build.
export const API_BASE_URL = import.meta.env.VITE_API_URL ?? "/api";
