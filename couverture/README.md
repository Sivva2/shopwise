# Rapports de Couverture de Tests

## Backend (JaCoCo)

Le rapport de couverture backend est généré automatiquement lors de l'exécution des tests.

### Génération locale
```bash
cd backend
mvn test jacoco:report
```

Le rapport HTML est généré dans : `backend/target/site/jacoco/index.html`

### Via CI/CD
Le rapport est disponible en artifact dans GitHub Actions :
- Aller dans l'onglet **Actions**
- Sélectionner le dernier workflow exécuté
- Télécharger l'artifact **backend-coverage-report**

### Résultat actuel : **60% de couverture**

---

## Frontend (Karma/Istanbul)

Le rapport de couverture frontend est généré automatiquement lors de l'exécution des tests.

### Génération locale
```bash
cd frontend
npm install
npm run test:ci
```

Le rapport HTML est généré dans : `frontend/coverage/shopwise-frontend/index.html`

### Via CI/CD
Le rapport est disponible en artifact dans GitHub Actions :
- Aller dans l'onglet **Actions**
- Sélectionner le dernier workflow exécuté
- Télécharger l'artifact **frontend-coverage-report**

---

## Note

Les rapports HTML ne sont pas versionnés dans Git car ils sont générés dynamiquement.
Ils sont accessibles via :
1. Exécution locale des tests
2. Artifacts de la pipeline CI/CD (GitHub Actions)
