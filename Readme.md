# Book Review API

A comprehensive RESTful API for managing books, authors, reviews, and ratings built with Spring Boot, PostgreSQL, and Spring Security.

## 📋 Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Database Schema](#database-schema)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [User Roles & Authentication](#user-roles--authentication)
- [Docker Setup](#docker-setup)
- [API Endpoints](#api-endpoints)
- [Contributing](#contributing)

## ✨ Features

- **User Management**: Registration, authentication, and role-based access control
- **Book Management**: CRUD operations for books with author and genre associations
- **Review System**: Users can write and manage reviews for books
- **Rating System**: Star-based rating system for books
- **Author Management**: Manage author information and their books
- **Genre Classification**: Organize books by genres
- **RESTful API**: Well-structured REST endpoints with proper HTTP status codes
- **API Documentation**: Interactive Swagger UI documentation
- **Security**: Spring Security with role-based authentication
- **Database Integration**: PostgreSQL with JPA/Hibernate

## 🛠 Technology Stack

- **Backend**: Spring Boot 3.x
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Build Tool**: Maven
- **Java Version**: 17
- **Containerization**: Docker

## 🗄 Database Schema

View the complete Entity Relationship Diagram here:
[📊 ER Diagram](https://drive.google.com/file/d/1mVpoIMgu0DvyDATOGJlqUrcympVxof4J/view?usp=sharing)

### Main Entities:
- **Users**: System users with role-based access
- **Books**: Book information with authors and genres
- **Authors**: Author details and their publications
- **Genres**: Book categorization
- **Reviews**: User reviews for books
- **Ratings**: Numerical ratings for books
- **Roles**: User permission levels

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

### Swagger UI
Interactive API documentation is available at:
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

The Swagger interface provides:
- Complete API endpoint documentation
- Request/response schemas
- Interactive testing capabilities
- Authentication examples

## 🔐 User Roles & Authentication

The application uses Spring Security with Basic Authentication and role-based access control.

### Default User Accounts

| Username | Password | Role | Permissions |
|----------|----------|------|-------------|
| \`admin_user\` | \`admin123\` | ADMIN | Full system access, user management |
| \`author_user\` | \`author123\` | AUTHOR | Manage own books, reviews, ratings |
| \`regular_user\` | \`user123\` | USER | Read access, create reviews/ratings |

### Authentication
Use HTTP Basic Authentication with the credentials above:
\`\`\`bash
# Example using curl
curl -u admin_user:admin123 http://localhost:8080/api/users
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

**Get all books:**
\`\`\`bash
curl -u regular_user:user123 http://localhost:8080/api/books
\`\`\`

**Create a new review:**
\`\`\`bash
curl -X POST -u regular_user:user123 \
-H "Content-Type: application/json" \
-d '{"bookId": 1, "rating": 5, "comment": "Great book!"}' \
http://localhost:8080/api/reviews
\`\`\`

## 🏗 Project Structure

\`\`\`
src/main/java/com/example/book_review/
├── config/          # Configuration classes
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

# Swagger Configuration
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/api-docs

# Logging
logging.level.com.example.book_review=DEBUG
\`\`\`

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (\`git checkout -b feature/amazing-feature\`)
3. Commit your changes (\`git commit -m 'Add some amazing feature'\`)
4. Push to the branch (\`git push origin feature/amazing-feature\`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions:
- Create an issue in the repository
- Contact: support@bookreview.com

---

**Happy Reading! 📚**
