# Use Node.js LTS version as the base image
FROM node:20-alpine

# Set the working directory
WORKDIR /usr/src/app

# Copy the package.json and package-lock.json
COPY package*.json ./

# Install dependencies
RUN npm install

# Ensure Angular CLI is available globally
RUN npm install -g @angular/cli

# Copy the application source code
COPY . .

# Expose the port Angular uses
EXPOSE 4200

# Start the Angular application
CMD ["npm", "start"]