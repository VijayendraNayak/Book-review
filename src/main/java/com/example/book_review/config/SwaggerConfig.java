package com.example.book_review.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Book Review API")
                        .version("1.0")
                        .description("""
                            ## Book Review API Documentation
                            
                            API for managing books, reviews, and ratings with role-based access control.
                            
                            ### Authentication
                            This API uses JWT (JSON Web Token) for authentication. To access protected endpoints:
                            
                            1. **Register/Login**: Use `/api/users/register` or `/api/users/login` to get a JWT token
                            2. **Authorize**: Click the 🔒 **Authorize** button below and enter: `Bearer <your-jwt-token>`
                            3. **Access APIs**: You can now access endpoints based on your role permissions
                            
                            ### Roles and Permissions
                            - **ADMIN**: Full access to all endpoints including user management
                            - **AUTHOR**: Can manage books, authors, genres, and view reviews
                            - **USER**: Can view books, create reviews and ratings
                            
                            ### Quick Start
                            1. Register a new user with `/api/users/register`
                            2. Login with `/api/users/login` to get your JWT token
                            3. Use the Authorize button to set your token
                            4. Start exploring the APIs!
                            """)
                        .contact(new Contact()
                                .name("Book Review API")
                                .email("support@bookreview.com")))
                .servers(List.of(
                        new Server().url(baseUrl).description("API Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token in format: Bearer <token>")));
    }
}

