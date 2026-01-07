FROM node:22.20.0-alpine AS build
WORKDIR /app

ENV NODE_OPTIONS="--max-old-space-size=4096"

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]