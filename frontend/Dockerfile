FROM node:25-alpine AS builder

WORKDIR /app
COPY package.json /app/package.json
RUN npm install
COPY . .
RUN npm run build

FROM alpine:latest
WORKDIR /app
COPY --from=builder /app/dist ./dist