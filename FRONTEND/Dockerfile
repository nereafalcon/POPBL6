# Step 1: Build the app
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
# SonarQube: COPY . . es seguro porque se implementa .dockerignore
COPY . . # NOSONAR
COPY .npmrc ./
RUN npm run build

# Opcional: eliminar .npmrc para que no afecte pasos siguientes
RUN rm .npmrc

# Step 2: Serve with Nginx
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

USER nginx

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
