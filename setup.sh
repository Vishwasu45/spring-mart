#!/bin/bash

# SpringMart Project File Generator
# This script generates all remaining source files for the SpringMart project

set -e

PROJECT_ROOT="/Users/umashav1/.gemini/antigravity/playground/rapid-oort"
cd "$PROJECT_ROOT"

echo "🚀 Generating SpringMart project files..."

# Create directory structure
echo "📁 Creating directory structure..."
mkdir -p src/main/java/com/springmart/{config,controller/{api,view},dto,mapper,service,security,event,exception}
mkdir -p src/main/resources/{static/{css,js,images},templates/{admin,fragments}}
mkdir -p src/test/java/com/springmart/{service,controller}
mkdir -p src/integration-test/java/com/springmart

echo "✅ Directory structure created"

echo ""
echo "📝 Project setup complete!"
echo ""
echo "⚠️  IMPORTANT: Before running the application, you need to:"
echo "1. Set up OAuth2 credentials (Google and/or GitHub)"
echo "2. Create application-local.yml with your credentials"
echo "3. Start Docker services: docker-compose up -d"
echo ""
echo "📚 Next steps:"
echo "1. Review the implementation plan"
echo "2. Continue with remaining source files generation"
echo ""
