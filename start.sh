#!/bin/bash
# SpringMart - Quick Start Script
# This script helps you get started with SpringMart quickly
# Supports both Podman and Docker
set -e  # Exit on error
echo "🚀 SpringMart - Quick Start"
echo "============================"
echo ""
# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color
# Function to print colored messages
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}
print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}
print_error() {
    echo -e "${RED}✗ $1${NC}"
}
print_info() {
    echo -e "ℹ $1"
}
# Check prerequisites
echo "Checking prerequisites..."
echo ""
# Check for Podman or Docker
if command -v podman &> /dev/null; then
    print_success "Podman is installed"
    CONTAINER_CMD="podman"
    COMPOSE_CMD="podman-compose"

    # Check if podman-compose is available
    if command -v podman-compose &> /dev/null; then
        print_success "Podman Compose is installed"
    else
        print_warning "Podman Compose not found, will try docker-compose"
        COMPOSE_CMD="docker-compose"
    fi
elif command -v docker &> /dev/null; then
    print_success "Docker is installed"
    CONTAINER_CMD="docker"
    COMPOSE_CMD="docker-compose"

    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed. Please install it first."
        exit 1
    fi
    print_success "Docker Compose is installed"
else
    print_error "Neither Podman nor Docker is installed. Please install one of them first."
    exit 1
fi
# Check Java
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -ge 17 ]; then
        print_success "Java $JAVA_VERSION is installed"
    else
        print_error "Java 17 or higher is required. Current version: $JAVA_VERSION"
        exit 1
    fi
else
    print_error "Java is not installed. Please install Java 17 or higher."
    exit 1
fi
echo ""
echo "============================"
echo "Step 1: Starting containers..."
echo ""
# Check if containers are already running
if $CONTAINER_CMD ps | grep -q springmart-postgres; then
    print_info "PostgreSQL container is already running"
else
    print_info "Starting containers with $COMPOSE_CMD..."

    # Start Podman machine if using Podman and it's not running
    if [ "$CONTAINER_CMD" = "podman" ]; then
        if ! podman machine list 2>/dev/null | grep -q "Currently running"; then
            print_info "Starting Podman machine..."
            podman machine start 2>/dev/null || print_warning "Podman machine might already be running"
            sleep 5
        fi
    fi

    $COMPOSE_CMD up -d

    print_info "Waiting for PostgreSQL to be ready (10 seconds)..."
    sleep 10

    print_success "Containers started successfully"
fi
echo ""
echo "============================"
echo "Step 2: Building the application..."
echo ""
# Build the application
print_info "Building with Gradle (this may take a few minutes)..."
./gradlew clean build -x test
if [ $? -eq 0 ]; then
    print_success "Build successful"
else
    print_error "Build failed. Check the error messages above."
    exit 1
fi
echo ""
echo "============================"
echo "Step 3: Starting the application..."
echo ""
print_info "Starting SpringMart application..."
print_warning "Press Ctrl+C to stop the application"
echo ""
echo "Once started, access:"
echo "  Homepage:  http://localhost:8080"
echo "  API Docs:  http://localhost:8080/swagger-ui.html"
echo ""
echo "============================"
echo ""
# Run the application
./gradlew bootRun
