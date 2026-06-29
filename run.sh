#!/bin/bash

echo "🚀 Starting Financial Transaction Service Ecosystem"
echo "--------------------------------------------------"
echo "1. Building the application and spinning up containers via docker-compose..."

docker compose up --build -d

echo ""
echo "✅ Application and Database are starting up!"
echo "📍 API will be available at: http://localhost:8080/api"
echo "📍 Swagger UI will be available at: http://localhost:8080/api/swagger-ui.html"
echo ""
echo "Use 'docker-compose logs -f financial-app' to see the logs."
