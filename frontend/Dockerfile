FROM node:24-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
EXPOSE 4200
# --host 0.0.0.0 so the dev server is reachable from outside the container
CMD ["npx", "ng", "serve", "--host", "0.0.0.0", "--poll", "1000"]
