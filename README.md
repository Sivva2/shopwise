# 🛒 ShopWise - Plateforme de Gestion pour Commerces

[![CI/CD Pipeline](https://github.com/USERNAME/shopwise/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/USERNAME/shopwise/actions/workflows/ci-cd.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

ShopWise est une solution SaaS moderne destinée aux commerces de proximité, permettant de centraliser la gestion des clients, des rendez-vous et de la fidélisation.

## 📋 Table des matières

- [Fonctionnalités](#-fonctionnalités)
- [Stack Technique](#-stack-technique)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Workflow Git](#-workflow-git)
- [Déploiement](#-déploiement)
- [Structure du Projet](#-structure-du-projet)
- [API Documentation](#-api-documentation)
- [Tests](#-tests)
- [CI/CD](#-cicd)

---

## ✨ Fonctionnalités

### Gestion des Clients
- Création et mise à jour des fiches clients
- Recherche et filtrage
- Historique des rendez-vous par client

### Gestion des Rendez-vous
- Création de rendez-vous avec client, date, heure et service
- Filtrage par date, statut ou client
- Mise à jour du statut (Planifié → Honoré/Annulé)

### Programme de Fidélité
- Attribution automatique des points lors d'un RDV honoré
- Consultation du solde et historique des transactions
- Utilisation et ajustement des points

---

## 🛠 Stack Technique

| Composant | Technologie |
|-----------|-------------|
| **Backend** | Java 17 + Spring Boot 3.2 |
| **Frontend** | Angular 17 (Standalone Components) |
| **Base de données** | MySQL 8.0 (H2 en dev) |
| **Conteneurisation** | Docker + Docker Compose |
| **CI/CD** | GitHub Actions |
| **Tests** | JUnit 5, Mockito, Jasmine, Karma |

---

## 📦 Prérequis

- **Java 17+** (pour le développement backend)
- **Node.js 20+** (pour le développement frontend)
- **Maven 3.9+** (build backend)
- **Docker & Docker Compose** (déploiement)
- **Git**

---

## 🚀 Installation

### 1. Cloner le repository

```bash
git clone https://github.com/USERNAME/shopwise.git
cd shopwise
```

### 2. Développement Local

#### Backend (Spring Boot)

```bash
cd backend

# Installer les dépendances et lancer
mvn spring-boot:run

# L'API est accessible sur http://localhost:8080
# Console H2: http://localhost:8080/h2-console
```

#### Frontend (Angular)

```bash
cd frontend

# Installer les dépendances
npm install

# Lancer le serveur de développement
npm start

# L'application est accessible sur http://localhost:4200
```

### 3. Avec Docker (Recommandé)

```bash
# Lancer tous les services
docker-compose up -d

# Vérifier les logs
docker-compose logs -f

# Arrêter les services
docker-compose down
```

**URLs après déploiement Docker :**
- Frontend: http://localhost:80
- Backend API: http://localhost:8080
- Base de données: localhost:3306

---

## 🔀 Workflow Git

Ce projet utilise le **Git Flow** simplifié :

### Branches principales

| Branche | Description |
|---------|-------------|
| `main` | Code en production, stable |
| `develop` | Branche d'intégration pour le développement |

### Branches de travail

| Type | Convention | Exemple |
|------|------------|---------|
| Feature | `feature/issue-XX-description` | `feature/issue-1-gestion-clients` |
| Bugfix | `bugfix/issue-XX-description` | `bugfix/issue-5-fix-validation` |
| Hotfix | `hotfix/issue-XX-description` | `hotfix/issue-10-critical-fix` |

### Workflow de développement

```bash
# 1. Créer une branche depuis develop
git checkout develop
git pull origin develop
git checkout -b feature/issue-1-gestion-clients

# 2. Développer et commiter (lier à l'issue)
git add .
git commit -m "feat: implémentation gestion clients #1"

# 3. Pousser et créer une Pull Request
git push origin feature/issue-1-gestion-clients

# 4. Après merge, supprimer la branche locale
git checkout develop
git pull origin develop
git branch -d feature/issue-1-gestion-clients
```

### Convention de commits

```
<type>: <description> #<issue-number>

Types:
- feat: Nouvelle fonctionnalité
- fix: Correction de bug
- docs: Documentation
- style: Formatage
- refactor: Refactoring
- test: Ajout de tests
- chore: Maintenance
```

---

## 🐳 Déploiement

### Déploiement avec Docker Compose

#### 1. Configuration

Créer un fichier `.env` à la racine :

```env
# Base de données
MYSQL_ROOT_PASSWORD=your_root_password
MYSQL_DATABASE=shopwise
MYSQL_USER=shopwise
MYSQL_PASSWORD=your_secure_password

# Docker Hub (pour CI/CD)
DOCKER_USERNAME=your_dockerhub_username
VERSION=latest
```

#### 2. Build et démarrage

```bash
# Build des images
docker-compose build

# Démarrage en arrière-plan
docker-compose up -d

# Vérifier l'état des services
docker-compose ps

# Voir les logs
docker-compose logs -f backend
docker-compose logs -f frontend
```

#### 3. Vérification

```bash
# Tester le backend
curl http://localhost:8080/api/clients

# Accéder au frontend
open http://localhost:80
```

#### 4. Arrêt et nettoyage

```bash
# Arrêter les services
docker-compose down

# Arrêter et supprimer les volumes (reset DB)
docker-compose down -v
```

### Déploiement manuel des images

```bash
# Build backend
cd backend
docker build -t shopwise-backend:latest .

# Build frontend
cd frontend
docker build -t shopwise-frontend:latest .

# Push vers Docker Hub
docker tag shopwise-backend:latest username/shopwise-backend:latest
docker push username/shopwise-backend:latest

docker tag shopwise-frontend:latest username/shopwise-frontend:latest
docker push username/shopwise-frontend:latest
```

---

## 📁 Structure du Projet

```
shopwise/
├── .github/
│   └── workflows/
│       └── ci-cd.yml          # Pipeline CI/CD
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/shopwise/
│   │   │   │   ├── controller/    # REST Controllers
│   │   │   │   ├── service/       # Business Logic
│   │   │   │   ├── repository/    # Data Access
│   │   │   │   ├── entity/        # JPA Entities
│   │   │   │   ├── dto/           # Data Transfer Objects
│   │   │   │   └── exception/     # Exception Handling
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/                  # Tests unitaires
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/        # Angular Components
│   │   │   ├── services/          # HTTP Services
│   │   │   └── models/            # TypeScript Interfaces
│   │   ├── environments/
│   │   └── styles.scss
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
├── bdd/
│   ├── schema_base_de_donnees.pdf  # Schéma ERD
│   └── script.sql                   # Script de création
├── couverture/                      # Rapports de couverture
├── docker-compose.yml
└── README.md
```

---

## 📚 API Documentation

### Endpoints Clients

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/clients` | Liste tous les clients |
| GET | `/api/clients/{id}` | Détail d'un client |
| GET | `/api/clients/search?query=` | Recherche de clients |
| POST | `/api/clients` | Créer un client |
| PUT | `/api/clients/{id}` | Modifier un client |
| DELETE | `/api/clients/{id}` | Supprimer un client |

### Endpoints Rendez-vous

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/appointments` | Liste (avec filtres optionnels) |
| GET | `/api/appointments/{id}` | Détail d'un RDV |
| GET | `/api/appointments/client/{id}` | RDV d'un client |
| POST | `/api/appointments` | Créer un RDV |
| PUT | `/api/appointments/{id}` | Modifier un RDV |
| PATCH | `/api/appointments/{id}/status` | Changer le statut |
| DELETE | `/api/appointments/{id}` | Supprimer un RDV |

### Endpoints Services

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/services` | Liste tous les services |
| GET | `/api/services/active` | Services actifs uniquement |
| POST | `/api/services` | Créer un service |
| PUT | `/api/services/{id}` | Modifier un service |
| DELETE | `/api/services/{id}` | Supprimer un service |

### Endpoints Fidélité

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/loyalty/client/{id}/balance` | Solde du client |
| GET | `/api/loyalty/client/{id}/transactions` | Historique |
| POST | `/api/loyalty/client/{id}/redeem` | Utiliser des points |
| POST | `/api/loyalty/client/{id}/adjust` | Ajuster des points |

---

## 🧪 Tests

### Backend

```bash
cd backend

# Lancer les tests
mvn test

# Lancer les tests avec rapport de couverture
mvn test jacoco:report

# Le rapport est généré dans: target/site/jacoco/index.html
```

### Frontend

```bash
cd frontend

# Lancer les tests
npm test

# Lancer les tests en mode CI avec couverture
npm run test:ci

# Le rapport est généré dans: coverage/shopwise-frontend/index.html
```

### Objectif de couverture : **60% minimum**

---

## ⚙️ CI/CD

La pipeline GitHub Actions effectue automatiquement :

### Sur chaque Push/PR

1. **Backend** : Build Maven + Tests + Rapport JaCoCo
2. **Frontend** : Build Angular + Tests + Rapport Karma
3. **Quality Gate** : Agrégation des rapports

### Sur merge vers `main`

4. **Docker** : Build et push des images vers Docker Hub

### Configuration requise

Ajouter ces secrets dans GitHub :

| Secret | Description |
|--------|-------------|
| `DOCKER_USERNAME` | Username Docker Hub |
| `DOCKER_PASSWORD` | Token Docker Hub |

### Télécharger les rapports de couverture

Les rapports sont disponibles en tant qu'artifacts dans l'onglet "Actions" de GitHub.

---

## 👥 Équipe

- **ShopWise SAS** - Développement et maintenance

## 📄 Licence

Ce projet est sous licence MIT - voir le fichier [LICENSE](LICENSE) pour plus de détails.

---

<p align="center">
  <strong>ShopWise</strong> - Logiciel de gestion pour commerces 🛒
</p>
