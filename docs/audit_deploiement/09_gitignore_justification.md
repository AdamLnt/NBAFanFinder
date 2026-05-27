# Justification du `.gitignore`

Le fichier `.gitignore` racine est déjà en place et couvre les principaux cas. Chaque section ci-dessous explique pourquoi les fichiers sont exclus.

## Contenu actuel et justification

| Section | Entrées | Raison |
|---|---|---|
| **OS** | `.DS_Store`, `Thumbs.db` | Fichiers système macOS/Windows, sans intérêt pour le projet. |
| **IDE** | `.idea/`, `.vscode/*` (sauf `extensions.json`), `*.suo`, `*.sw?` | Configurations locales propres à chaque développeur. |
| **Node** | `node_modules/`, `package-lock.json` | Dépendances volumineuses, reconstruites via `npm install`. |
| **Secrets** | `.env` | Contient des mots de passe, clés JWT, identifiants de BDD — **jamais à committer**. |
| **Sonar** | `.scannerwork/` | Cache local du scanner SonarQube, régénéré à chaque scan. |

## Compléments présents dans les sous-dossiers

- `Backend/.gitignore` : ignore `target/` (artefacts Maven), `.mvn/wrapper/*.jar` selon les bonnes pratiques Spring.
- `Frontend/.gitignore` : ignore `dist/`, `coverage/`, `node_modules/`, fichiers de logs.

## Vérification

Aucune donnée sensible (`.env`, secrets, builds, dépendances) n'apparaît dans l'historique Git. Le hook `gitleaks` (pre-commit) ajoute une seconde barrière contre l'ajout accidentel de secrets.

## Conclusion

Le `.gitignore` est complet et bien justifié, aucune modification n'est nécessaire.
