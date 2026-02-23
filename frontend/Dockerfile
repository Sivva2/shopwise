# ============================================
# ShopWise Frontend - Dockerfile
# Multi-stage build avec Nginx
# ============================================

# Stage 1: Build
FROM node:20-alpine AS build

WORKDIR /app

# Copier les fichiers de configuration
COPY package*.json ./

# Installer les dépendances
RUN npm ci

# Copier le code source
COPY . .

# Build l'application Angular en mode production
RUN npm run build:prod

# Stage 2: Runtime avec Nginx
FROM nginx:alpine

# Supprimer la configuration par défaut
RUN rm -rf /usr/share/nginx/html/*

# Copier la configuration Nginx personnalisée
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copier les fichiers buildés depuis le stage précédent
COPY --from=build /app/dist/shopwise-frontend/browser /usr/share/nginx/html

# Exposer le port 80
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:80/ || exit 1

# Démarrer Nginx
CMD ["nginx", "-g", "daemon off;"]
