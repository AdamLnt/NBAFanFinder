# Build stage
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json .
RUN npm ci
COPY . .
RUN npm run build

# Serve stage
FROM nginx:1.27-alpine
RUN apk upgrade --no-cache
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf.template
RUN chown -R nginx:nginx /usr/share/nginx/html && \
    chown -R nginx:nginx /var/cache/nginx && \
    chown -R nginx:nginx /var/log/nginx && \
    chown -R nginx:nginx /etc/nginx/conf.d && \
    touch /var/run/nginx.pid && \
    chown nginx:nginx /var/run/nginx.pid
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD wget -q --spider "http://localhost:${PORT}/" || exit 1
USER nginx
# Le port est injecte au runtime via la variable d'env PORT (Railway le fait auto pour les
# services publics ; en local docker-compose, on le force a 8080).
CMD ["/bin/sh", "-c", "envsubst '${PORT} ${BACKEND_HOST} ${BACKEND_PORT}' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf && exec nginx -g 'daemon off;'"]
