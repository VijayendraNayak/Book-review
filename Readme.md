# Book Review API - Step-by-Step Testing Guide

## 🚀 Getting Started

**Base URL**: `https://book-review-wpkn.onrender.com`

## Step 1: Register a New User

First, let's register a new user with the AUTHOR role:

### Request
```bash
curl -X POST https://book-review-wpkn.onrender.com/api/users/register \
-H "Content-Type: application/json" \
-d '{
    "username": "test2",
    "email": "test2@gmail.com",
    "password": "test123",
    "role": "AUTHOR"
}'
```

### Expected Response
```json
{
    "id": 123,
    "username": "test2",
    "email": "test2@gmail.com",
    "role": "AUTHOR",
    "createdAt": "2025-07-11T10:30:00Z"
}
```

## Step 2: Login with Your New Account

Use the credentials from registration to get a JWT token:

### Request
```bash
curl -X POST https://book-review-wpkn.onrender.com/api/users/login \
-H "Content-Type: application/json" \
-d '{
    "username": "test2",
    "password": "test123"
}'
```

### Expected Response
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0MiIsInJvbGUiOiJST0xFX0FVVEhPUiIsImlhdCI6MTY4OTk2MzAwMCwiZXhwIjoxNjkwMDQ5NDAwfQ.abc123def456...",
    "type": "Bearer",
    "username": "test2",
    "email": "test2@gmail.com",
    "role": "ROLE_AUTHOR"
}
```

**Important**: Copy the `token` value from the response - you'll need it for all protected API calls.

## Step 3: Use the JWT Token for Protected Endpoints

Now you can access protected endpoints using the Bearer token. Replace `YOUR_JWT_TOKEN` with the actual token from Step 2.

### 3.1 Get Your User Profile

```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
https://book-review-wpkn.onrender.com/api/users/profile
```

### 3.2 Get All Books (Public - No Token Required)

```bash
curl https://book-review-wpkn.onrender.com/api/books
```

### 3.3 Create a New Book (AUTHOR Role Required)

```bash
curl -X POST https://book-review-wpkn.onrender.com/api/books \
-H "Authorization: Bearer YOUR_JWT_TOKEN" \
-H "Content-Type: application/json" \
-d '{
    "title": "My New Book",
    "description": "A fascinating book about technology",
    "authorId": 1,
    "genreId": 1
}'
```

### 3.4 Create a Review for a Book

```bash
curl -X POST https://book-review-wpkn.onrender.com/api/reviews \
-H "Authorization: Bearer YOUR_JWT_TOKEN" \
-H "Content-Type: application/json" \
-d '{
    "bookId": 1,
    "rating": 5,
    "comment": "Excellent book! Highly recommend it."
}'
```

### 3.5 Get All Authors (Public)

```bash
curl https://book-review-wpkn.onrender.com/api/authors
```

### 3.6 Get All Genres (Public)

```bash
curl https://book-review-wpkn.onrender.com/api/genres
```

## Step 4: Test Different User Roles

The API has pre-configured test accounts you can also use:

### Default Admin User
```bash
curl -X POST https://book-review-wpkn.onrender.com/api/users/login \
-H "Content-Type: application/json" \
-d '{
    "username": "admin_user",
    "password": "admin123"
}'
```

### Default Regular User
```bash
curl -X POST https://book-review-wpkn.onrender.com/api/users/login \
-H "Content-Type: application/json" \
-d '{
    "username": "regular_user",
    "password": "user123"
}'
```

## 🔐 Role-Based Access Control

| Role | Permissions |
|------|-------------|
| **ADMIN** | Full access to all resources |
| **AUTHOR** | Can manage books, authors, reviews, and ratings |
| **USER** | Can view books, create reviews and ratings |

## 🛠️ Using Postman (Recommended)

For easier testing, use the provided Postman collection:

1. **Visit the Postman Workspace**: [Book Review API Collection](https://crimson-zodiac-594948.postman.co/workspace/My-Workspace~9e659192-6e99-48a3-8371-d3ffaa1761a6/collection/24909042-40d1ebc2-e36b-4628-a34d-4eabc2151caa?action=share&creator=24909042)

2. **Fork the collection** to your workspace

3. **Set up environment variables**:
   - `base_url`: `https://book-review-wpkn.onrender.com`
   - `jwt_token`: (will be set automatically after login)

4. **Test the flow**:
   - Register → Login → Copy Token → Test Protected Endpoints

## 🐳 Running Locally with Docker

If you want to run the API locally:

```bash
# Pull the Docker image
docker pull vijayendranayak/book-review-api:latest

# Run with your database
docker run -p 8080:8080 \
-e SPRING_DATASOURCE_URL=jdbc:postgresql://your-db:5432/bookreview \
-e SPRING_DATASOURCE_USERNAME=your-username \
-e SPRING_DATASOURCE_PASSWORD=your-password \
-e JWT_SECRET=your-secret-key \
vijayendranayak/book-review-api:latest
```

Then use `http://localhost:8080` as your base URL instead.

## 📋 Testing Checklist

- [ ] Register new user with AUTHOR role
- [ ] Login and receive JWT token
- [ ] Access user profile with token
- [ ] Create a new book (AUTHOR permission)
- [ ] Create a review for a book
- [ ] Test public endpoints (books, authors, genres)
- [ ] Try accessing admin endpoints (should be restricted)

## 🔍 Troubleshooting

**Common Issues:**

1. **401 Unauthorized**: Check if your JWT token is valid and properly formatted
2. **403 Forbidden**: Your role doesn't have permission for this endpoint
3. **Token Expired**: Login again to get a new token (expires in 24 hours)
4. **Invalid JSON**: Ensure your request body is properly formatted JSON

**Token Format:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Make sure there's a space between "Bearer" and the token!