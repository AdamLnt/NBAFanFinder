# Scripts et commandes de lancement

## Stack complète (Docker)

```bash
docker compose up --build       # build + lance MySQL + Backend + Frontend
docker compose down             # stop, conserve les volumes
docker compose down -v          # stop + supprime le volume MySQL
```

- Frontend : http://localhost
- Backend : http://localhost:8080

## Backend (Java 17, Maven)

| Commande | Rôle |
|---|---|
| `./mvnw spring-boot:run` | Lance le backend en local |
| `./mvnw verify` | Compile, teste, génère le rapport JaCoCo |
| `./mvnw spotless:check` | Vérifie le formatage |
| `./mvnw spotless:apply` | Corrige automatiquement le formatage |

Rapport de couverture : `Backend/target/site/jacoco/index.html`

## Frontend (Node 20, npm)

| Commande | Rôle |
|---|---|
| `npm install` | Installe les dépendances |
| `npm run dev` | Lance le serveur de dev Vite |
| `npm run build` | Build de production |
| `npm run preview` | Sert le build localement |
| `npm run lint` | Lance ESLint |
| `npm run format` | Corrige le formatage (Prettier) |
| `npm run format:check` | Vérifie le formatage |
| `npm run test` | Lance les tests Vitest (one-shot) |
| `npm run test:watch` | Tests en mode watch |
| `npm run test:coverage` | Tests + rapport de couverture |

Rapport de couverture : `Frontend/coverage/index.html`

## Qualité / sécurité

| Commande | Rôle |
|---|---|
| `pre-commit install` | Installe les hooks Git |
| `pre-commit run --all-files` | Lance tous les hooks sur tout le projet |
| `docker compose -f docker-compose.sonar.yml up -d` | Démarre SonarQube local (http://localhost:9000) |

## Vérification effectuée

- `docker compose up --build` : la stack se lance correctement, les 3 services démarrent.
- Routes principales et affichage des données fonctionnels (cf. `02_verification_locale.md`).
- `./mvnw verify` et `npm run test` exécutables sans erreur (tests configurés, couverture à compléter).

## Conclusion

Les scripts standards (`dev`, `start`/`run`, `build`, `test`) sont tous présents et fonctionnels, côté Backend comme Frontend.
