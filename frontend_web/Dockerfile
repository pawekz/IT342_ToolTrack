# syntax=docker/dockerfile:1

# Use the official Node.js image as the base image
ARG NODE_VERSION=22.13.1
FROM node:${NODE_VERSION}-slim AS builder

# Set the working directory
WORKDIR /app

# Copy package.json and package-lock.json for dependency installation
COPY --link package.json package-lock.json ./

# Install dependencies with caching
#RUN --mount=type=cache,target=/root/.npm \
#    npm ci

RUN npm install

# Copy the application source code
COPY --link . .

# Build the application
RUN npm run build

# Prepare the production image
FROM node:${NODE_VERSION}-slim AS final

# Set the working directory
WORKDIR /app

# Copy the built application and necessary files from the builder stage
COPY --from=builder /app/dist ./dist
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package*.json ./

# Set environment variables
ENV NODE_ENV=production
ENV NODE_OPTIONS="--max-old-space-size=4096"

# Create a non-root user and switch to it
RUN useradd -m appuser
USER appuser

# Expose the port the app runs on
EXPOSE 3000

# Define the command to run the application
CMD ["npm", "run", "preview"]