#!/bin/bash

# QuickCache Application Startup Script
# This script starts all required services in the correct order

echo "🚀 Starting QuickCache Services..."

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker and try again."
    exit 1
fi

# Check if docker-compose is available
if ! command -v docker-compose &> /dev/null; then
    print_error "docker-compose is not installed. Please install it and try again."
    exit 1
fi

print_status "Stopping any existing containers..."
docker-compose down

print_status "Starting MySQL and Redis containers..."
docker-compose up -d mysql redis

print_status "Waiting for MySQL to be ready..."
timeout=60
while ! docker-compose exec mysql mysqladmin ping -h"localhost" --silent; do
    timeout=$((timeout - 1))
    if [ $timeout -eq 0 ]; then
        print_error "MySQL failed to start within 60 seconds"
        exit 1
    fi
    sleep 1
done

print_status "Waiting for Redis to be ready..."
timeout=30
while ! docker-compose exec redis redis-cli ping > /dev/null 2>&1; do
    timeout=$((timeout - 1))
    if [ $timeout -eq 0 ]; then
        print_error "Redis failed to start within 30 seconds"
        exit 1
    fi
    sleep 1
done

print_status "MySQL and Redis are ready!"

# Check if Maven is available
if command -v mvn &> /dev/null; then
    print_status "Compiling application with Maven..."
    mvn clean compile -q
    
    if [ $? -eq 0 ]; then
        print_status "Starting QuickCache application..."
        mvn spring-boot:run
    else
        print_error "Maven compilation failed"
        exit 1
    fi
else
    print_warning "Maven not found. Please run the application manually with:"
    echo "  mvn spring-boot:run"
    echo ""
    print_status "Services are running:"
    echo "  - MySQL: localhost:3306"
    echo "  - Redis: localhost:6379"
fi

print_status "Services started successfully!"
echo ""
echo "📋 Service Information:"
echo "  - Application: http://localhost:8080"
echo "  - API Documentation: http://localhost:8080/swagger-ui.html"
echo "  - MySQL: localhost:3306 (username: root, password: 137127117)"
echo "  - Redis: localhost:6379"
echo ""
echo "🔧 Useful commands:"
echo "  - Stop services: docker-compose down"
echo "  - View logs: docker-compose logs -f"
echo "  - MySQL console: docker-compose exec mysql mysql -uroot -p137127117 quickcache"
echo "  - Redis console: docker-compose exec redis redis-cli"
