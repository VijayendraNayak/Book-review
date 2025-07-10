package com.example.book_review.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test-endpoints")
@Tag(name = "Testing", description = "Endpoints for testing API functionality and permissions")
public class ApiTestController {

    @GetMapping("/guide")
    @Operation(summary = "Get testing guide", description = "Returns comprehensive testing guide for the API")
    public Map<String, Object> getTestingGuide() {
        Map<String, Object> guide = new HashMap<>();

        guide.put("title", "Book Review API Testing Guide");
        guide.put("version", "1.0");
        guide.put("description", "Comprehensive guide for testing role-based access control");

        Map<String, String> testUsers = new HashMap<>();
        testUsers.put("admin", "admin_user:admin123 (Full access to all endpoints)");
        testUsers.put("author", "author_user:author123 (Can create books, authors, genres + user functions)");
        testUsers.put("user", "regular_user:user123 (Can create reviews/ratings + read all content)");
        guide.put("testUsers", testUsers);

        Map<String, String> publicEndpoints = new HashMap<>();
        publicEndpoints.put("books", "GET /api/books - View all books");
        publicEndpoints.put("authors", "GET /api/authors - View all authors");
        publicEndpoints.put("genres", "GET /api/genres - View all genres");
        publicEndpoints.put("reviews", "GET /api/reviews - View all reviews");
        publicEndpoints.put("ratings", "GET /api/ratings - View all ratings");
        guide.put("publicEndpoints", publicEndpoints);

        Map<String, Object> rolePermissions = new HashMap<>();

        Map<String, String> adminPermissions = new HashMap<>();
        adminPermissions.put("create", "Books, Authors, Genres, Reviews, Ratings");
        adminPermissions.put("read", "Everything including Users and Roles");
        adminPermissions.put("update", "All resources");
        adminPermissions.put("delete", "All resources");
        rolePermissions.put("ADMIN", adminPermissions);

        Map<String, String> authorPermissions = new HashMap<>();
        authorPermissions.put("create", "Books, Authors, Genres, Reviews, Ratings");
        authorPermissions.put("read", "Books, Authors, Genres, Reviews, Ratings (NOT Users/Roles)");
        authorPermissions.put("update", "Own content");
        authorPermissions.put("delete", "Own content");
        rolePermissions.put("AUTHOR", authorPermissions);

        Map<String, String> userPermissions = new HashMap<>();
        userPermissions.put("create", "Reviews, Ratings only");
        userPermissions.put("read", "Books, Authors, Genres, Reviews, Ratings");
        userPermissions.put("update", "Own reviews and ratings");
        userPermissions.put("delete", "Own reviews and ratings");
        rolePermissions.put("USER", userPermissions);

        guide.put("rolePermissions", rolePermissions);

        Map<String, String> testingSteps = new HashMap<>();
        testingSteps.put("1", "Start with public endpoints (no auth required)");
        testingSteps.put("2", "Test admin user (should access everything)");
        testingSteps.put("3", "Test author user (should create books/genres but not access users)");
        testingSteps.put("4", "Test regular user (should only create reviews/ratings)");
        testingSteps.put("5", "Verify forbidden operations return 403");
        guide.put("testingSteps", testingSteps);

        Map<String, String> importantUrls = new HashMap<>();
        importantUrls.put("swagger", "http://localhost:8080/swagger-ui/index.html");
        importantUrls.put("apiDocs", "http://localhost:8080/v3/api-docs");
        importantUrls.put("currentUser", "GET /api/test-endpoints/current-user (requires auth)");
        importantUrls.put("permissions", "GET /api/test-endpoints/check-permissions (requires auth)");
        guide.put("importantUrls", importantUrls);

        return guide;
    }

    @GetMapping("/current-user")
    @Operation(summary = "Get current authenticated user", description = "Returns information about the currently authenticated user")
    public Map<String, Object> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", auth.getName());
        userInfo.put("authorities", auth.getAuthorities());
        userInfo.put("authenticated", auth.isAuthenticated());
        userInfo.put("principal", auth.getPrincipal().getClass().getSimpleName());

        return userInfo;
    }

    @GetMapping("/check-permissions")
    @Operation(summary = "Check user permissions", description = "Returns detailed permission information for the current user")
    public Map<String, Object> checkPermissions() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> permissions = new HashMap<>();
        permissions.put("username", auth.getName());
        permissions.put("roles", auth.getAuthorities());

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isAuthor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_AUTHOR"));
        boolean isUser = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));

        Map<String, Boolean> roleCheck = new HashMap<>();
        roleCheck.put("isAdmin", isAdmin);
        roleCheck.put("isAuthor", isAuthor);
        roleCheck.put("isUser", isUser);
        permissions.put("roleCheck", roleCheck);

        Map<String, Boolean> canAccess = new HashMap<>();
        canAccess.put("createBooks", isAdmin || isAuthor);
        canAccess.put("createAuthors", isAdmin || isAuthor);
        canAccess.put("createGenres", isAdmin || isAuthor);
        canAccess.put("createReviews", isAdmin || isAuthor || isUser);
        canAccess.put("createRatings", isAdmin || isAuthor || isUser);
        canAccess.put("accessUsers", isAdmin);
        canAccess.put("accessRoles", isAdmin);
        permissions.put("canAccess", canAccess);

        Map<String, String> recommendations = new HashMap<>();
        if (isAdmin) {
            recommendations.put("message", "You have full access to all endpoints");
            recommendations.put("testSuggestion", "Try creating books, authors, genres, reviews, and accessing user management");
        } else if (isAuthor) {
            recommendations.put("message", "You can create content but cannot access user management");
            recommendations.put("testSuggestion", "Try creating books, authors, genres, reviews. Test that /api/users returns 403");
        } else if (isUser) {
            recommendations.put("message", "You can create reviews and ratings only");
            recommendations.put("testSuggestion", "Try creating reviews and ratings. Test that /api/books POST returns 403");
        }
        permissions.put("recommendations", recommendations);

        return permissions;
    }
}
