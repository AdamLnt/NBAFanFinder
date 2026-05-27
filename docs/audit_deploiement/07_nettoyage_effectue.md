# Nettoyage du projet

## État initial

Le projet est globalement bien organisé : séparation claire `Backend/` et `Frontend/`, racine lisible, dossier `docs/` dédié à la documentation.

## Éléments identifiés

| Élément | Statut | Action |
|---|---|---|
| `commitlint.config` (sans extension, encodé UTF-16) | Doublon de `commitlint.config.js` | À supprimer (le `.js` est utilisé par Node) |
| `node_modules/` (racine + Frontend) | Présent localement | Conservé, déjà ignoré par `.gitignore` |
| `Backend/target/` | Build Maven | Conservé localement, déjà ignoré par `.gitignore` |
| `Frontend/dist/`, `Frontend/coverage/` | Builds et rapports | Conservés localement, déjà ignorés |
| Fichiers `.DS_Store`, `Thumbs.db` | Aucun trouvé | Rien à supprimer |
| Fichiers temporaires (`*.tmp`, `*.bak`) | Aucun trouvé | Rien à supprimer |

## Rangement

- Le code source est correctement réparti :
  - `Backend/src/` pour le code Java
  - `Frontend/src/` pour le code React/TS
  - `docs/` pour toute la documentation
- Les fichiers de configuration racine (`docker-compose.yml`, `.pre-commit-config.yaml`, `sonar-project.properties`) sont à leur place.

## Renommage

Aucun fichier au nom incohérent n'a été détecté.

## Conclusion

Le projet est déjà propre. Seul le doublon `commitlint.config` mérite d'être supprimé pour éviter toute confusion.
