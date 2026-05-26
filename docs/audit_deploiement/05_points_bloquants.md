# Points bloquants

Liste des principaux points à traiter avant le déploiement.

## 1. Absence d'un fichier `.env.example`

- **Impact** : un nouveau développeur ou l'environnement de déploiement ne sait pas quelles variables doivent être renseignées.
- **Action prévue** : créer un `.env.example` à la racine listant toutes les variables attendues (sans valeurs sensibles).

## 2. Couverture des tests à compléter

- **Impact** : risque de régressions non détectées lors du déploiement.
- **Action prévue** : ajouter les tests manquants côté back (services, contrôleurs) et front (composants principaux) pour atteindre un seuil de couverture acceptable.

## 3. Configuration de production non distincte du local

- **Impact** : `docker-compose.yml` actuel est orienté développement (volumes, ports exposés, secrets en clair).
- **Action prévue** : prévoir un `docker-compose.prod.yml` ou un fichier de configuration dédié à la prod.

## 4. Pas de stratégie de gestion des secrets en production

- **Impact** : les secrets sont aujourd'hui dans `.env` local, non adapté à un environnement de déploiement.
- **Action prévue** : définir une solution (GitHub Secrets, variables d'environnement de l'hébergeur, vault…) avant la mise en ligne.

## 5. Absence de documentation de déploiement

- **Impact** : le README couvre le développement local mais pas la procédure de mise en production.
- **Action prévue** : ajouter une section ou un document dédié décrivant les étapes de déploiement (build, push image, configuration serveur).
