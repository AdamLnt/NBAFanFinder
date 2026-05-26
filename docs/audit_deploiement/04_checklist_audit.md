# Checklist d'audit

| Critère | Oui / Non / Partiel | Commentaire |
|---|---|---|
| Le projet se lance localement sans erreur | Oui | `docker compose up --build` lance la stack complète. |
| Structure du projet claire et lisible | Oui | Séparation Backend / Frontend nette, racine bien organisée. |
| Dépendances installées et fonctionnelles | Oui | Maven pour le back, npm pour le front, builds OK. |
| Scripts de lancement disponibles | Oui | Documentés dans le README (Docker, Maven, npm). |
| Documentation présente (README) | Oui | README complet : prérequis, lancement, tests, CI/CD, Sonar. |
| Variables d'environnement gérées proprement | Partiel | `.env` à la racine, ignoré par Git, mais pas de `.env.example` fourni. |
| Fichiers sensibles non versionnés | Oui | `.env` et secrets bien listés dans `.gitignore`, gitleaks actif. |
| Tests présents | Partiel | Tests back (JUnit + JaCoCo) et front (Vitest) configurés, couverture à compléter. |
| CI/CD configurée | Oui | GitHub Actions : tests, build Docker, Trivy, SonarQube. |
| Sécurité de base (hooks, scans) | Oui | pre-commit, gitleaks, Trivy, SonarQube quality gate. |
