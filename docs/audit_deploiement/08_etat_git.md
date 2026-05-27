# État du dépôt Git

## Vérification

Le projet est **déjà un dépôt Git** initialisé. Aucune initialisation supplémentaire n'a été nécessaire.

## Informations

- **Remote** : `https://github.com/AdamLnt/NBAFanFinder`
- **Branche principale** : `main`
- **Branche courante** : `docs/audit_deploiement`
- **État du working tree** : propre (aucune modification non commitée au moment de la vérification)

## Historique récent (extrait)

```
a58dbcf Merge branch 'docs/audit_deploiement'
c47374a feat: seance 1
74cf2dd feat: security changes
5f535ac Merge pull request #21 from AdamLnt/feature/add_map
d2cb227 feat: add map
6d06de0 Merge pull request #3 from AdamLnt/feature/docker-and-pipelines
```

## Workflow

- Branches de fonctionnalités : `feature/*`
- Branches de documentation : `docs/*`
- Merge vers `main` via Pull Request, avec CI obligatoire (tests, Docker, Trivy, SonarQube).
- Hooks actifs : `pre-commit` (gitleaks, lint, format) et `commitlint` (convention de commit).
