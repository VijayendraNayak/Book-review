# Book Review API

A comprehensive RESTful API for managing books, authors, reviews, and ratings built with Spring Boot, PostgreSQL, JWT authentication, and Spring Security.

## 📋 Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Database Schema](#database-schema)
- [Live Demo](#live-demo)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [User Roles & Authentication](#user-roles--authentication)
- [Docker Setup](#docker-setup)
- [Docker Hub](#docker-hub)
- [GitHub Actions CI/CD](#github-actions-cicd)
- [API Endpoints](#api-endpoints)
- [Testing the API](#testing-the-api)
- [Contributing](#contributing)

## ✨ Features

- **User Management**: Registration, authentication with JWT tokens, and role-based access control
- **Book Management**: CRUD operations for books with author and genre associations
- **Review System**: Users can write and manage reviews for books
- **Rating System**: Star-based rating system for books
- **Author Management**: Manage author information and their books
- **Genre Classification**: Organize books by genres
- **RESTful API**: Well-structured REST endpoints with proper HTTP status codes
- **JWT Security**: Stateless authentication with JSON Web Tokens
- **Database Integration**: PostgreSQL with JPA/Hibernate
- **Docker Support**: Containerized deployment with multi-stage builds
- **CI/CD Pipeline**: Automated builds and deployments with GitHub Actions

## 🛠 Technology Stack

- **Backend**: Spring Boot 3.x
- **Database**: PostgreSQL
- **Authentication**: JWT (JSON Web Tokens)
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security
- **Build Tool**: Maven
- **Java Version**: 17
- **Containerization**: Docker
- **CI/CD**: GitHub Actions
- **Container Registry**: Docker Hub

## 🗄 Database Schema

View the complete Entity Relationship Diagram here:
[🔗 ER Diagram](https://drive.google.com/file/d/1mVpoIMgu0DvyDATOGJlqUrcympVxof4J/view?usp=sharing)

### Main Entities:
- **Users**: System users with role-based access
- **Books**: Book information with authors and genres
- **Authors**: Author details and their publications
- **Genres**: Book categorization
- **Reviews**: User reviews for books
- **Ratings**: Numerical ratings for books
- **Roles**: User permission levels

## 🌐 Live Demo

The API is deployed and accessible at:
- **Base URL**: `https://book-review-wpkn.onrender.com`

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Docker (optional)

### Local Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd book-review
   ```

2. **Database Setup**
   - Create a PostgreSQL database
   - Update database credentials in `src/main/resources/application.properties`

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Build and Run**
   ```bash
   # Build the project
   mvn clean install

   # Run the application
   mvn spring-boot:run
   ```

4. **Access the Application**
   - API Base URL: `http://localhost:8080`

## 📚 API Documentation

### Postman Collection
Test the API endpoints directly using our Postman collection:
- **Postman Workspace**: [📮 Book Review API Collection](https://crimson-zodiac-594948.postman.co/workspace/My-Workspace~9e659192-6e99-48a3-8371-d3ffaa1761a6/collection/24909042-40d1ebc2-e36b-4628-a34d-4eabc2151caa?action=share&creator=24909042)

The Postman collection includes:
- Pre-configured requests for all endpoints
- Environment variables for easy switching between local and production
- Authentication examples with JWT tokens
- Sample request bodies and expected responses

## 🔐 User Roles & Authentication

The application uses JWT (JSON Web Tokens) for stateless authentication and role-based access control.

### Default User Accounts

| Username | Password | Role | Permissions |
|----------|----------|------|-------------|
| `admin_user` | `admin123` | ADMIN | Full system access, user management |
| `author_user` | `author123` | AUTHOR | Manage own books, reviews, ratings |
| `regular_user` | `user123` | USER | Read access, create reviews/ratings |

### JWT Authentication Flow

1. **Login to get JWT token:**
   ```bash
   curl -X POST https://book-review-wpkn.onrender.com/api/users/login \
   -H "Content-Type: application/json" \
   -d '{"username": "admin_user", "password": "admin123"}'
   ```

2. **Response:**
   ```json
   {
   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
   "type": "Bearer",
   "username": "admin_user",
   "email": "admin@example.com",
   "role": "ROLE_ADMIN"
   }
   ```

3. **Use token in subsequent requests:**
   ```bash
   curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
   https://book-review-wpkn.onrender.com/api/users
   ```

### Role Permissions
- **ADMIN**: Complete CRUD access to all resources
- **AUTHOR**: Can manage books, authors, and respond to reviews
- **USER**: Can browse books, create reviews and ratings

## 🐳 Docker Setup

The application uses a multi-stage Docker build for optimized production images.

### Docker Build Process

The Docker build includes two stages:
1. **Build Stage**: Uses Maven with OpenJDK 17 to compile and package the application
2. **Runtime Stage**: Uses OpenJDK 17 slim image for a smaller production container

### Quick Start with Docker

1. **Pull from Docker Hub (Recommended)**
   ```bash
   docker pull vijayendranayak/book-review-api:latest
   ```

2. **Run with Docker**
   ```bash
   docker run -p 8080:8080 \
   -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/your_db \
   -e SPRING_DATASOURCE_USERNAME=your_username \
   -e SPRING_DATASOURCE_PASSWORD=your_password \
   -e JWT_SECRET=your_jwt_secret_key \
   vijayendranayak/book-review-api:latest
   ```

### Build from Source

1. **Clone and build locally**
   ```bash
   git clone <repository-url>
   cd book-review
   docker build -t book-review-api .
   ```

2. **Run the locally built image**
   ```bash
   docker run -p 8080:8080 \
   -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/your_db \
   -e SPRING_DATASOURCE_USERNAME=your_username \
   -e SPRING_DATASOURCE_PASSWORD=your_password \
   -e JWT_SECRET=your_jwt_secret_key \
   book-review-api
   ```

### Docker Compose Setup

Create a `docker-compose.yml` file for easy development:

```yaml
version: '3.8'
services:
   postgres:
      image: postgres:15-alpine
      environment:
         POSTGRES_DB: bookreview
         POSTGRES_USER: postgres
         POSTGRES_PASSWORD: password
      ports:
         - "5432:5432"
      volumes:
         - postgres_data:/var/lib/postgresql/data

   book-review-api:
      image: vijayendranayak/book-review-api:latest
      ports:
         - "8080:8080"
      environment:
         SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/bookreview
         SPRING_DATASOURCE_USERNAME: postgres
         SPRING_DATASOURCE_PASSWORD: password
         JWT_SECRET: mySecretKeyForJWTTokenGenerationThatShouldBeLongEnoughForSecurity
         JWT_EXPIRATION: 86400
      depends_on:
         - postgres

volumes:
   postgres_data:
```

Run with Docker Compose:
```bash
docker-compose up -d
```

## 🏗 Docker Hub

The application is automatically built and published to Docker Hub.

### Available Images

- **Docker Hub Repository**: [vijayendranayak/book-review-api](https://hub.docker.com/r/vijayendranayak/book-review-api)
- **Latest Version**: `vijayendranayak/book-review-api:latest`
- **Tagged Versions**: Each commit is tagged with its SHA for version tracking

### Image Details

- **Base Image**: OpenJDK 17 JDK Slim
- **Build Tool**: Maven 3.8.4
- **Image Size**: ~200MB (optimized multi-stage build)
- **Exposed Port**: 8080
- **Working Directory**: `/app`

### Pulling the Image

```bash
# Pull latest version
docker pull vijayendranayak/book-review-api:latest

# Pull specific version (by commit SHA)
docker pull vijayendranayak/book-review-api:abc1234
```

## 🔄 GitHub Actions CI/CD

The project includes automated CI/CD pipeline using GitHub Actions.

### Pipeline Features

- **Automated Building**: Triggered on every push to main branch
- **Docker Build**: Multi-stage build with caching for faster builds
- **Docker Hub Push**: Automatic publishing to Docker Hub
- **Version Tagging**: Each build is tagged with both `latest` and commit SHA
- **Build Caching**: Uses GitHub Actions cache for faster subsequent builds

### Workflow Configuration

The pipeline performs the following steps:
1. Checkout source code
2. Set up Docker Buildx for advanced features
3. Login to Docker Hub using secrets
4. Build Docker image with cache optimization
5. Push to Docker Hub with multiple tags

### Required Secrets

Set these secrets in your GitHub repository:
- `DOCKERHUB_USERNAME`: Your Docker Hub username
- `DOCKERHUB_TOKEN`: Your Docker Hub access token

### Build Status

Every commit to the main branch triggers:
- Automated Docker build
- Push to Docker Hub registry
- Deployment-ready container images

## 🔗 API Endpoints

### Core Resources

| Resource | Endpoint | Methods | Description |
|----------|----------|---------|-------------|
| **Users** | `/api/users` | GET, POST, PUT, DELETE | User management |
| **Books** | `/api/books` | GET, POST, PUT, DELETE | Book catalog |
| **Authors** | `/api/authors` | GET, POST, PUT, DELETE | Author information |
| **Genres** | `/api/genres` | GET, POST, PUT, DELETE | Book categories |
| **Reviews** | `/api/reviews` | GET, POST, PUT, DELETE | Book reviews |
| **Ratings** | `/api/ratings` | GET, POST, PUT, DELETE | Book ratings |
| **Roles** | `/api/roles` | GET, POST, PUT, DELETE | User roles |

### Example Requests

**Login and get JWT token:**
```bash
curl -X POST https://book-review-wpkn.onrender.com/api/users/login \
-H "Content-Type: application/json" \
-d '{"username": "regular_user", "password": "user123"}'
```

**Get all books (public endpoint):**
```bash
curl https://book-review-wpkn.onrender.com/api/books
```

**Create a new review (requires JWT token):**
```bash
curl -X POST https://book-review-wpkn.onrender.com/api/reviews \
-H "Authorization: Bearer YOUR_JWT_TOKEN" \
-H "Content-Type: application/json" \
-d '{"bookId": 1, "rating": 5, "comment": "Great book!"}'
```

**Get user profile (requires JWT token):**
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
https://book-review-wpkn.onrender.com/api/users/profile
```

**Register a new user:**
```bash
curl -X POST https://book-review-wpkn.onrender.com/api/users/register \
-H "Content-Type: application/json" \
-d '{
"username": "newuser",
"email": "newuser@example.com",
"password": "password123"
}'
```

**Get all authors (public endpoint):**
```bash
curl https://book-review-wpkn.onrender.com/api/authors
```

**Create a new book (requires ADMIN or AUTHOR role):**
```bash
curl -X POST https://book-review-wpkn.onrender.com/api/books \
-H "Authorization: Bearer YOUR_JWT_TOKEN" \
-H "Content-Type: application/json" \
-d '{
"title": "New Book",
"description": "A great new book",
"authorId": 1,
"genreId": 1
}'
```

## 🏗 Project Structure

```
src/main/java/com/example/book_review/
├── config/          # Configuration classes (Security, JWT)
├── controllers/     # REST controllers
├── dto/            # Data Transfer Objects
├── models/         # JPA entities
├── repository/     # Data repositories
└── services/       # Business logic
```

## 🔧 Configuration

### Application Properties
Key configuration options in `application.properties`:

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/bookreview
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT Configuration
jwt.secret=mySecretKeyForJWTTokenGenerationThatShouldBeLongEnoughForSecurity
jwt.expiration=86400

# Logging
logging.level.com.example.book_review=DEBUG
```

### Docker Environment Variables

When running in Docker, override these properties with environment variables:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/your-db
SPRING_DATASOURCE_USERNAME=your-username
SPRING_DATASOURCE_PASSWORD=your-password
JWT_SECRET=your-very-long-secret-key-for-jwt-signing
JWT_EXPIRATION=86400
```

## 🚀 Deployment

### Render Deployment
The application is configured for deployment on Render:

1. Connect your GitHub repository to Render
2. Set environment variables:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
   - `JWT_SECRET`
   - `JWT_EXPIRATION`
3. Deploy automatically on push to main branch

### Docker Deployment

Deploy using the pre-built Docker image:

```bash
# Using Docker Hub image
docker run -d \
  --name book-review-api \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://your-db:5432/bookreview \
  -e SPRING_DATASOURCE_USERNAME=your-username \
  -e SPRING_DATASOURCE_PASSWORD=your-password \
  -e JWT_SECRET=your-secret-key \
  vijayendranayak/book-review-api:latest
```

### Environment Variables
Required environment variables for production:
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/your-db
SPRING_DATASOURCE_USERNAME=your-username
SPRING_DATASOURCE_PASSWORD=your-password
JWT_SECRET=your-very-long-secret-key-for-jwt-signing
JWT_EXPIRATION=86400
```

## 🧪 Testing the API

### Using Postman (Recommended)

1. **Import the collection:**
   - Visit our [Postman Workspace](https://crimson-zodiac-594948.postman.co/workspace/My-Workspace~9e659192-6e99-48a3-8371-d3ffaa1761a6/collection/24909042-40d1ebc2-e36b-4628-a34d-4eabc2151caa?action=share&creator=24909042)
   - Fork the collection to your workspace
   - Set up environment variables for local/production testing

2. **Test authentication:**
   - Use the "Login" request with default credentials
   - Copy the JWT token from the response
   - Use it in subsequent requests

### Using Docker for Testing

1. **Quick test setup:**
   ```bash
   # Run with Docker Compose
   docker-compose up -d
   
   # Wait for services to start
   sleep 30
   
   # Test the API
   curl http://localhost:8080/api/books
   ```

2. **Test with specific database:**
   ```bash
   docker run -d \
     --name test-api \
     -p 8080:8080 \
     -e SPRING_DATASOURCE_URL=jdbc:postgresql://your-test-db:5432/test_db \
     -e SPRING_DATASOURCE_USERNAME=test_user \
     -e SPRING_DATASOURCE_PASSWORD=test_pass \
     -e JWT_SECRET=test_secret_key \
     vijayendranayak/book-review-api:latest
   ```

### Using cURL

1. **Test user registration:**
   ```bash
   curl -X POST https://book-review-wpkn.onrender.com/api/users/register \
   -H "Content-Type: application/json" \
   -d '{
   "username": "testuser",
   "email": "test@example.com",
   "password": "testpass123"
   }'
   ```

2. **Login with default admin user:**
   ```bash
   curl -X POST https://book-review-wpkn.onrender.com/api/users/login \
   -H "Content-Type: application/json" \
   -d '{"username": "admin_user", "password": "admin123"}'
   ```

3. **Access protected endpoint:**
   ```bash
   curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
   https://book-review-wpkn.onrender.com/api/users/profile
   ```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development with Docker

For development, you can use Docker to ensure consistency:

```bash
# Build development image
docker build -t book-review-api:dev .

# Run with development database
docker-compose -f docker-compose.dev.yml up -d

# Run tests in container
docker run --rm -v $(pwd):/app book-review-api:dev mvn test
```

## 🏷️ Version History

- **Latest**: Always available at `vijayendranayak/book-review-api:latest`
- **Versioned**: Each commit tagged with SHA on Docker Hub
- **Stable**: Production-ready builds with comprehensive testing

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

**Happy Reading! 📚**