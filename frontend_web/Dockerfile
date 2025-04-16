FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Production stage
FROM node:18-alpine
WORKDIR /app
COPY --from=build /app/package*.json ./
COPY --from=build /app/dist ./dist
COPY --from=build /app/node_modules ./node_modules
COPY --from=build /app/vite.config.js ./vite.config.js

EXPOSE 4173
# Set explicit host binding
CMD ["npm", "run", "preview", "--", "--host", "0.0.0.0"]