# SpringMart - Cloud-Native E-Commerce Platform

> 🎉 **STATUS: 75% Complete & Fully Functional!** All core features are implemented and working. Ready to run!
> 
> 🚀 **Quick Start**: Run `./start.sh` or see [RUNNING_GUIDE.md](RUNNING_GUIDE.md) for detailed instructions.

A production-ready e-commerce platform built with Spring Boot 3, demonstrating enterprise-level Spring concepts, AWS integration, OAuth 2.0 security, event-driven architecture, and Thymeleaf-based frontend.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![AWS](https://img.shields.io/badge/AWS-S3%20%7C%20SQS%20%7C%20SNS-yellow)
![License](https://img.shields.io/badge/License-MIT-green)

## 📋 Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [AWS Deployment](#aws-deployment)
- [Learning Objectives](#learning-objectives)
- [Project Structure](#project-structure)

## ✨ Features

### Core Functionality
- **Product Management**: CRUD operations for products with categories and images
- **Shopping Cart**: Add, update, remove items with real-time calculations
- **Order Processing**: Complete checkout flow with order tracking
- **User Reviews**: Rate and review products
- **Search & Filter**: Advanced product search and category filtering

### Security
- **OAuth 2.0 Authentication**: Login with Google and GitHub
- **JWT Tokens**: Stateless API authentication
- **Role-Based Access Control**: USER, SELLER, and ADMIN roles
- **Spring Security**: Comprehensive security configuration

### Event-Driven Architecture
- **AWS SQS**: Asynchronous order processing
- **AWS SNS**: Notification system for order updates
- **@Async Processing**: Non-blocking event handling

### Cloud Integration
- **AWS S3**: Product image storage
- **AWS RDS**: PostgreSQL database (production)
- **AWS ElastiCache**: Redis caching (production)
- **LocalStack**: Local AWS services for development

### Performance
- **Redis Caching**: Product and category caching
- **Database Indexing**: Optimized queries
- **Lazy Loading**: Efficient JPA relationships

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Frontend Layer                           │
│              (Thymeleaf + Bootstrap 5)                       │
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────┐
│                  Security Layer                              │
│         (Spring Security + OAuth2 + JWT)                     │
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────┐
│                 Controller Layer                             │
│         (REST APIs + View Controllers)                       │
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────┐
│                  Service Layer                               │
│    (Business Logic + Event Publishing)                       │
└────────┬───────────┴───────────┬────────────────────────────┘
         │                       │
┌────────▼──────────┐   ┌───────▼────────────────────────────┐
│  Repository Layer │   │      Event Layer                    │
│   (Spring Data    │   │  (SQS Publishers/Listeners)         │
│       JPA)        │   └─────────────────────────────────────┘
└────────┬──────────┘
         │
┌────────▼──────────────────────────────────────────────────┐
│              Data Layer                                    │
│  PostgreSQL + Redis + S3                                   │
└────────────────────────────────────────────────────────────┘
```

## 🛠️ Technology Stack

### Backend
- **Java 17** - Latest LTS version
- **Spring Boot 3.2.0** - Application framework
- **Spring Data JPA** - Data access layer
- **Spring Security** - Authentication & authorization
- **Spring OAuth2 Client** - OAuth2 integration
- **Flyway** - Database migrations
- **Lombok** - Boilerplate reduction
- **MapStruct** - DTO mapping

### Frontend
- **Thymeleaf** - Server-side templating
- **Bootstrap 5** - UI framework
- **JavaScript** - Client-side interactions

### Database & Caching
- **PostgreSQL 16** - Primary database
- **Redis** - Caching layer

### AWS Services
- **S3** - Object storage for images
- **SQS** - Message queuing
- **SNS** - Pub/sub notifications
- **RDS** - Managed PostgreSQL (production)
- **ElastiCache** - Managed Redis (production)

### Development Tools
- **Gradle** - Build automation
- **Docker & Docker Compose** - Containerization
- **LocalStack** - Local AWS emulation
- **SpringDoc OpenAPI** - API documentation
- **JUnit 5** - Testing framework
- **Testcontainers** - Integration testing

## 📦 Prerequisites

- **Java 17** or higher
- **Docker** and **Docker Compose**
- **Gradle** (or use the wrapper `./gradlew`)
- **OAuth2 Credentials** (Google and/or GitHub)

## 🚀 Getting Started

### 1. Clone the Repository

```bash
cd /Users/umashav1/.gemini/antigravity/playground/rapid-oort
```

### 2. Set Up OAuth2 Credentials

#### Google OAuth2
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable Google+ API
4. Create OAuth 2.0 credentials
5. Add authorized redirect URI: `http://localhost:8080/login/oauth2/code/google`

#### GitHub OAuth2
1. Go to [GitHub Developer Settings](https://github.com/settings/developers)
2. Create a new OAuth App
3. Set Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`

### 3. Configure Environment Variables

Create `src/main/resources/application-local.yml`:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: YOUR_GOOGLE_CLIENT_ID
            client-secret: YOUR_GOOGLE_CLIENT_SECRET
          github:
            client-id: YOUR_GITHUB_CLIENT_ID
            client-secret: YOUR_GITHUB_CLIENT_SECRET

app:
  jwt:
    secret: your-super-secret-jwt-key-at-least-256-bits-long-change-this
```

### 4. Start Infrastructure Services

```bash
docker-compose up -d
```

This starts:
- PostgreSQL on port 5432
- Redis on port 6379
- LocalStack on port 4566

### 5. Build the Application

```bash
./gradlew clean build
```

### 6. Run the Application

```bash
./gradlew bootRun --args='--spring.profiles.active=dev,local'
```

The application will be available at:
- **Web UI**: http://localhost:8080
- **API Docs**: http://localhost:8080/swagger-ui.html
- **API JSON**: http://localhost:8080/api-docs

## ⚙️ Configuration

### Profiles

- **dev**: Development profile (uses local databases and LocalStack)
- **prod**: Production profile (uses AWS services)
- **local**: Local overrides for OAuth2 credentials (not committed to git)

### Key Configuration Files

- `application.yml` - Main configuration
- `application-dev.yml` - Development settings
- `application-prod.yml` - Production settings
- `application-local.yml` - Local overrides (git-ignored)

## 🏃 Running the Application

### Using Gradle

```bash
# Run with dev profile
./gradlew bootRun

# Run with specific profile
./gradlew bootRun --args='--spring.profiles.active=dev,local'

# Run tests
./gradlew test

# Run integration tests
./gradlew integrationTest

# Generate test coverage
./gradlew jacocoTestReport
```

### Using Docker

```bash
# Build Docker image
docker build -t springmart:latest .

# Run container
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e GOOGLE_CLIENT_ID=your-id \
  -e GOOGLE_CLIENT_SECRET=your-secret \
  springmart:latest
```

## 📚 API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

### Key API Endpoints

#### Products
- `GET /api/products` - List all products (paginated)
- `GET /api/products/{id}` - Get product details
- `POST /api/products` - Create product (SELLER, ADMIN)
- `PUT /api/products/{id}` - Update product (SELLER, ADMIN)
- `DELETE /api/products/{id}` - Delete product (ADMIN)

#### Cart
- `GET /api/cart` - Get user's cart
- `POST /api/cart` - Add item to cart
- `PUT /api/cart/{id}` - Update cart item
- `DELETE /api/cart/{id}` - Remove from cart

#### Orders
- `POST /api/orders` - Create order
- `GET /api/orders` - List user orders
- `GET /api/orders/{id}` - Get order details
- `PUT /api/orders/{id}/status` - Update order status (ADMIN)

## 🧪 Testing

### Unit Tests

```bash
./gradlew test
```

### Integration Tests

```bash
./gradlew integrationTest
```

### Test Coverage

```bash
./gradlew jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

## ☁️ AWS Deployment

See [DEPLOYMENT.md](DEPLOYMENT.md) for detailed AWS deployment instructions.

### Quick Overview

1. **RDS PostgreSQL**: Create database instance
2. **ElastiCache Redis**: Create Redis cluster
3. **S3 Bucket**: Create bucket for product images
4. **SQS Queue**: Create queue for order events
5. **SNS Topic**: Create topic for notifications
6. **Elastic Beanstalk**: Deploy application

## 🎓 Learning Objectives

This project demonstrates the following Spring Boot concepts:

### Core Spring Concepts
- ✅ **Dependency Injection**: Constructor injection with Lombok
- ✅ **Inversion of Control**: Spring container management
- ✅ **Auto-configuration**: Spring Boot starters
- ✅ **Profiles**: Environment-specific configuration
- ✅ **Component Scanning**: Automatic bean discovery

### Spring Data JPA
- ✅ **Entity Relationships**: @OneToMany, @ManyToOne, @ManyToMany
- ✅ **Custom Queries**: Method names, @Query, JPQL
- ✅ **Pagination & Sorting**: Pageable interface
- ✅ **Auditing**: @CreatedDate, @LastModifiedDate
- ✅ **Database Migrations**: Flyway

### Spring Security
- ✅ **OAuth2 Client**: Google and GitHub login
- ✅ **JWT Authentication**: Stateless API security
- ✅ **Role-Based Authorization**: @PreAuthorize, hasRole()
- ✅ **Custom User Details**: OAuth2 user mapping
- ✅ **Security Filters**: Custom JWT filter

### Spring Web
- ✅ **REST Controllers**: @RestController, @RequestMapping
- ✅ **Request Validation**: @Valid, Bean Validation
- ✅ **Exception Handling**: @ControllerAdvice, @ExceptionHandler
- ✅ **DTOs**: Request/Response objects
- ✅ **Thymeleaf**: Server-side rendering

### Advanced Features
- ✅ **Caching**: @Cacheable, @CacheEvict with Redis
- ✅ **Async Processing**: @Async, @EnableAsync
- ✅ **Event-Driven**: SQS/SNS integration
- ✅ **File Upload**: Multipart files to S3
- ✅ **Transaction Management**: @Transactional

### Testing
- ✅ **Unit Tests**: Mockito, JUnit 5
- ✅ **Integration Tests**: Testcontainers
- ✅ **Security Tests**: @WithMockUser
- ✅ **Test Coverage**: JaCoCo

## 📁 Project Structure

```
springmart/
├── src/
│   ├── main/
│   │   ├── java/com/springmart/
│   │   │   ├── config/           # Configuration classes
│   │   │   ├── controller/       # REST & View controllers
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── entity/           # JPA Entities
│   │   │   ├── enums/            # Enumerations
│   │   │   ├── event/            # Event publishers/listeners
│   │   │   ├── exception/        # Custom exceptions
│   │   │   ├── mapper/           # MapStruct mappers
│   │   │   ├── repository/       # Spring Data repositories
│   │   │   ├── security/         # Security components
│   │   │   ├── service/          # Business logic
│   │   │   └── SpringMartApplication.java
│   │   └── resources/
│   │       ├── db/migration/     # Flyway migrations
│   │       ├── templates/        # Thymeleaf templates
│   │       ├── static/           # CSS, JS, images
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
│   └── test/                     # Unit & integration tests
├── build.gradle                  # Gradle build file
├── settings.gradle               # Gradle settings
├── docker-compose.yml            # Local infrastructure
├── Dockerfile                    # Production container
└── README.md                     # This file
```

## 🤝 Contributing

This is a learning project. Feel free to fork and experiment!

## 📄 License

MIT License - feel free to use this project for learning and portfolio purposes.

## 🙏 Acknowledgments

Built as a comprehensive Spring Boot learning project demonstrating:
- Enterprise application architecture
- Cloud-native development practices
- Modern security patterns
- Event-driven design
- Production-ready code quality

---

**Happy Learning! 🚀**

For questions or issues, please check the code comments and Spring Boot documentation.
