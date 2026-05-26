# Analyse de la structure

## Arborescence

```
NBAFanFinder/
├── Backend/                    # Application Spring Boot (Java 17, Maven)
│   ├── src/                    # Code source Java
│   ├── pom.xml                 # Dépendances Maven
│   └── Dockerfile              # Image Docker du backend
├── Frontend/                   # Application React + Vite + TypeScript
│   ├── src/                    # Code source React/TS
│   ├── package.json            # Dépendances npm
│   ├── nginx.conf              # Configuration Nginx (prod)
│   └── Dockerfile              # Image Docker du frontend
├── docs/                       # Documentation (dont audit de déploiement)
├── .github/workflows/          # Pipelines CI/CD GitHub Actions
├── .husky/                     # Hooks Git côté Node
├── docker-compose.yml          # Stack complète (MySQL + Back + Front)
├── docker-compose.sonar.yml    # Instance SonarQube locale
├── sonar-project.properties    # Configuration scanner Sonar
├── .pre-commit-config.yaml     # Hooks pre-commit (gitleaks, lint, format)
├── .gitignore                  # Ignore node_modules, .env, target/, etc.
├── .env                        # Variables sensibles (non versionné)
└── README.md                   # Documentation principale
```

## Fichiers importants repérés

- **README.md** : présent, complet (prérequis, lancement, tests, CI/CD).
- **.env** : présent à la racine, bien ignoré par `.gitignore`.
- **.gitignore** : couvre les secrets, `node_modules`, builds, IDE.
- **Dockerfile** : présent côté Backend et Frontend.
- **docker-compose.yml** : orchestration des 3 couches.
- **CI/CD** : `.github/workflows/` avec pipelines de tests, build, scan Trivy et SonarQube.

## Conclusion

La structure du projet est claire et lisible. La séparation Front / Back est nette, la documentation est présente, et les fichiers sensibles sont correctement gérés.
