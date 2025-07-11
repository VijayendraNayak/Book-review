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
- [API Endpoints](#api-endpoints)
- [Contributing](#contributing)

## ✨ Features

- **User Management**: Registration, authentication with JWT tokens, and role-based access control
- **Book Management**: CRUD operations for books with author and genre associations
- **Review System**: Users can write and manage reviews for books
- **Rating System**: Star-based rating system for books
- **Author Management**: Manage author information and their books
- **Genre Classification**: Organize books by genres
- **RESTful API**: Well-structured REST endpoints with proper HTTP status codes
- **API Documentation**: Interactive Swagger UI documentation
- **JWT Security**: Stateless authentication with JSON Web Tokens
- **Database Integration**: PostgreSQL with JPA/Hibernate

## 🛠 Technology Stack

- **Backend**: Spring Boot 3.x
- **Database**: PostgreSQL
- **Authentication**: JWT (JSON Web Tokens)
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Build Tool**: Maven
- **Java Version**: 17
- **Containerization**: Docker

## 🗄 Database Schema

View the complete Entity Relationship Diagram here:
[�� ER Diagram](https://drive.google.com/file/d/1mVpoIMgu0DvyDATOGJlqUrcympVxof4J/view?usp=sharing)

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
- **Swagger UI**: [https://book-review-wpkn.onrender.com/swagger-ui.html](https://book-review-wpkn.onrender.com/swagger-ui.html)
- **API Docs**: [https://book-review-wpkn.onrender.com/api-docs](https://book-review-wpkn.onrender.com/api-docs)

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Docker (optional)

### Local Setup

1. **Clone the repository**
   \`\`\`bash
   git clone <repository-url>
   cd book-review
   \`\`\`

2. **Database Setup**
   - Create a PostgreSQL database
   - Update database credentials in \`src/main/resources/application.properties\`

   \`\`\`properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   \`\`\`

3. **Build and Run**
   \`\`\`bash
   # Build the project
   mvn clean install

   # Run the application
   mvn spring-boot:run
   \`\`\`

4. **Access the Application**
   - API Base URL: \`http://localhost:8080\`
   - Swagger UI: \`http://localhost:8080/swagger-ui.html\`
   - API Docs: \`http://localhost:8080/api-docs\`

## 📚 API Documentation

### Swagger UI(wait for 20-30s for render's cold start)
Interactive API documentation is available at:
- **Production**: [https://book-review-wpkn.onrender.com/swagger-ui.html](https://book-review-wpkn.onrender.com/swagger-ui.html)
- **Local**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [https://book-review-wpkn.onrender.com/api-docs](https://book-review-wpkn.onrender.com/api-docs)

The Swagger interface provides:
- Complete API endpoint documentation
- Request/response schemas
- Interactive testing capabilities
- JWT authentication examples

## 🔐 User Roles & Authentication

The application uses JWT (JSON Web Tokens) for stateless authentication and role-based access control.

### Default User Accounts

| Username | Password | Role | Permissions |
|----------|----------|------|-------------|
| \`admin_user\` | \`admin123\` | ADMIN | Full system access, user management |
| \`author_user\` | \`author123\` | AUTHOR | Manage own books, reviews, ratings |
| \`regular_user\` | \`user123\` | USER | Read access, create reviews/ratings |

### JWT Authentication Flow

1. **Login to get JWT token:**
   \`\`\`bash
   curl -X POST https://book-review-wpkn.onrender.com/api/users/login \
   -H "Content-Type: application/json" \
   -d '{"username": "admin_user", "password": "admin123"}'
   \`\`\`

2. **Response:**
   \`\`\`json
   {
   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
   "type": "Bearer",
   "username": "admin_user",
   "email": "admin@example.com",
   "role": "ROLE_ADMIN"
   }
   \`\`\`

3. **Use token in subsequent requests:**
   \`\`\`bash
   curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
   https://book-review-wpkn.onrender.com/api/users
   \`\`\`

### Role Permissions
- **ADMIN**: Complete CRUD access to all resources
- **AUTHOR**: Can manage books, authors, and respond to reviews
- **USER**: Can browse books, create reviews and ratings

## 🐳 Docker Setup

### Build and Run with Docker

1. **Build the Docker image**
   \`\`\`bash
   docker build -t book-review-api .
   \`\`\`

2. **Run the container**
   \`\`\`bash
   docker run -p 8080:8080 \
   -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/your_db \
   -e SPRING_DATASOURCE_USERNAME=your_username \
   -e SPRING_DATASOURCE_PASSWORD=your_password \
   -e JWT_SECRET=your_jwt_secret_key \
   book-review-api
   \`\`\`

### Docker Compose (Recommended)

Create a \`docker-compose.yml\` file:
\`\`\`yaml
version: '3.8'
services:
app:
build: .
ports:
- "8080:8080"
environment:
- SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/bookreview
- SPRING_DATASOURCE_USERNAME=bookuser
- SPRING_DATASOURCE_PASSWORD=bookpass
- JWT_SECRET=mySecretKeyForJWTTokenGenerationThatShouldBeLongEnoughForSecurity
- JWT_EXPIRATION=86400
depends_on:
- db

db:
image: postgres:13
environment:
- POSTGRES_DB=bookreview
- POSTGRES_USER=bookuser
- POSTGRES_PASSWORD=bookpass
ports:
- "5432:5432"
volumes:
- postgres_data:/var/lib/postgresql/data

volumes:
postgres_data:
\`\`\`

Run with: \`docker-compose up -d\`

## 🔗 API Endpoints

### Core Resources

| Resource | Endpoint | Methods | Description |
|----------|----------|---------|-------------|
| **Users** | \`/api/users\` | GET, POST, PUT, DELETE | User management |
| **Books** | \`/api/books\` | GET, POST, PUT, DELETE | Book catalog |
| **Authors** | \`/api/authors\` | GET, POST, PUT, DELETE | Author information |
| **Genres** | \`/api/genres\` | GET, POST, PUT, DELETE | Book categories |
| **Reviews** | \`/api/reviews\` | GET, POST, PUT, DELETE | Book reviews |
| **Ratings** | \`/api/ratings\` | GET, POST, PUT, DELETE | Book ratings |
| **Roles** | \`/api/roles\` | GET, POST, PUT, DELETE | User roles |

### Example Requests

**Login and get JWT token:**
\`\`\`bash
curl -X POST https://book-review-wpkn.onrender.com/api/users/login \
-H "Content-Type: application/json" \
-d '{"username": "regular_user", "password": "user123"}'
\`\`\`

**Get all books (public endpoint):**
\`\`\`bash
curl https://book-review-wpkn.onrender.com/api/books
\`\`\`

**Create a new review (requires JWT token):**
\`\`\`bash
curl -X POST https://book-review-wpkn.onrender.com/api/reviews \
-H "Authorization: Bearer YOUR_JWT_TOKEN" \
-H "Content-Type: application/json" \
-d '{"bookId": 1, "rating": 5, "comment": "Great book!"}'
\`\`\`

**Get user profile (requires JWT token):**
\`\`\`bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
https://book-review-wpkn.onrender.com/api/users/profile
\`\`\`

**Register a new user:**
\`\`\`bash
curl -X POST https://book-review-wpkn.onrender.com/api/users/register \
-H "Content-Type: application/json" \
-d '{
"username": "newuser",
"email": "newuser@example.com",
"password": "password123"
}'
\`\`\`

**Get all authors (public endpoint):**
\`\`\`bash
curl https://book-review-wpkn.onrender.com/api/authors
\`\`\`

**Create a new book (requires ADMIN or AUTHOR role):**
\`\`\`bash
curl -X POST https://book-review-wpkn.onrender.com/api/books \
-H "Authorization: Bearer YOUR_JWT_TOKEN" \
-H "Content-Type: application/json" \
-d '{
"title": "New Book",
"description": "A great new book",
"authorId": 1,
"genreId": 1
}'
\`\`\`

## 🏗 Project Structure

\`\`\`
src/main/java/com/example/book_review/
├── config/          # Configuration classes (Security, JWT, Swagger)
├── controllers/     # REST controllers
├── dto/            # Data Transfer Objects
├── models/         # JPA entities
├── repository/     # Data repositories
└── services/       # Business logic
\`\`\`

## 🔧 Configuration

### Application Properties
Key configuration options in \`application.properties\`:

\`\`\`properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/bookreview
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT Configuration
jwt.secret=mySecretKeyForJWTTokenGenerationThatShouldBeLongEnoughForSecurity
jwt.expiration=86400

# Swagger Configuration
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/api-docs

# Logging
logging.level.com.example.book_review=DEBUG
\`\`\`

## 🚀 Deployment

### Render Deployment
The application is configured for deployment on Render:

1. Connect your GitHub repository to Render
2. Set environment variables:
   - \`SPRING_DATASOURCE_URL\`
   - \`SPRING_DATASOURCE_USERNAME\`
   - \`SPRING_DATASOURCE_PASSWORD\`
   - \`JWT_SECRET\`
   - \`JWT_EXPIRATION\`
3. Deploy automatically on push to main branch

### Environment Variables
Required environment variables for production:
\`\`\`bash
SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/your-db
SPRING_DATASOURCE_USERNAME=your-username
SPRING_DATASOURCE_PASSWORD=your-password
JWT_SECRET=your-very-long-secret-key-for-jwt-signing
JWT_EXPIRATION=86400
\`\`\`

## 🧪 Testing the API

### Using cURL

1. **Test user registration:**
   \`\`\`bash
   curl -X POST https://book-review-wpkn.onrender.com/api/users/register \
   -H "Content-Type: application/json" \
   -d '{
   "username": "testuser",
   "email": "test@example.com",
   "password": "testpass123"
   }'
   \`\`\`

2. **Login with default admin user:**
   \`\`\`bash
   curl -X POST https://book-review-wpkn.onrender.com/api/users/login \
   -H "Content-Type: application/json" \
   -d '{"username": "admin_user", "password": "admin123"}'
   \`\`\`

3. **Access protected endpoint:**
   \`\`\`bash
   curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
   https://book-review-wpkn.onrender.com/api/users/profile
   \`\`\`

### Using Swagger UI(wait for 20-30s beacuse of render's cold start)

Visit [https://book-review-wpkn.onrender.com/swagger-ui.html](https://book-review-wpkn.onrender.com/swagger-ui.html) to:
- Test all endpoints interactively
- View request/response schemas
- Authenticate using JWT tokens
- Explore the complete API documentation

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (\`git checkout -b feature/amazing-feature\`)
3. Commit your changes (\`git commit -m 'Add some amazing feature'\`)
4. Push to the branch (\`git push origin feature/amazing-feature\`)
5. Open a Pull Request



**Happy Reading! 📚**
