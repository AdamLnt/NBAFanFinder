# Variables d'environnement

## Démarche

Les variables sensibles et de configuration ont été repérées dans :

- `.env` (racine, non versionné)
- `docker-compose.yml` (interpolation `${VAR}`)
- `Backend/src/main/resources/application.properties` (placeholders `${VAR}`)
- `Frontend/vite.config.ts` et code TS (`import.meta.env.VITE_*`)

Un fichier **`.env.example`** a été créé à la racine pour servir de modèle (sans valeurs sensibles).

## Liste des variables

### Base de données MySQL

| Variable | Rôle |
|---|---|
| `MYSQL_ROOT_PASSWORD` | Mot de passe root MySQL |
| `MYSQL_DATABASE` | Nom de la BDD | 
| `MYSQL_USER` | Utilisateur applicatif | 
| `MYSQL_PASSWORD` | Mot de passe applicatif | 

### Backend Spring Boot

| Variable | Rôle | Exemple |
|---|---|---|
| `SPRING_DATASOURCE_URL` | URL JDBC de la BDD | `jdbc:mysql://mysql:3306/NBAFanFinder` |
| `SPRING_DATASOURCE_USERNAME` | Utilisateur BDD | `nbafanfinder_app` |
| `SPRING_DATASOURCE_PASSWORD` | Mot de passe BDD | `changeme_app_password` |
| `SPRING_JPA_DDL_AUTO` | Mode Hibernate (`validate`/`update`/`create-drop`) | `update` |

### Sécurité

| Variable | Rôle | Exemple |
|---|---|---|
| `JWT_SECRET` | Clé de signature des tokens (≥ 64 caractères) | chaîne aléatoire |
| `JWT_EXPIRATION` | Durée de vie du token (ms) | `86400000` |
| `CORS_ALLOWED_ORIGINS` | Origines autorisées (jamais `*`) | `http://localhost:5173` |
| `AUTH_COOKIE_SECURE` | `true` en prod (HTTPS obligatoire) | `false` |
| `AUTH_COOKIE_SAME_SITE` | Politique SameSite du cookie | `Lax` |

### Frontend

| Variable | Rôle | Exemple |
|---|---|---|
| `VITE_API_URL` | URL de l'API consommée par le front | `/api` |

## Bonnes pratiques

- `.env` est **ignoré par Git** (cf. `.gitignore`).
- `.env.example` doit être tenu à jour à chaque ajout de variable.
- En production, ne **jamais** utiliser un `.env` versionné : préférer GitHub Secrets, variables d'environnement de l'hébergeur, ou un vault.
- Le hook `gitleaks` (pre-commit) bloque l'ajout accidentel de secrets dans un commit.
