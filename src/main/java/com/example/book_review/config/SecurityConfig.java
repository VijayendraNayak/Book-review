package com.example.book_review.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserDetailsService customUserDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("=== Configuring JWT Security Filter Chain ===");

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints - no authentication required
                        .requestMatchers("/api/test-endpoints/**").permitAll()
                        .requestMatchers("/test/**").permitAll()

                        // Swagger UI endpoints - completely public for documentation access
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/v3/api-docs.yaml").permitAll()
                        .requestMatchers("/v3/api-docs").permitAll()
                        .requestMatchers("/api-docs/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()

                        // Authentication endpoints - must be public
                        .requestMatchers("/api/users/register").permitAll()
                        .requestMatchers("/api/users/login").permitAll()

                        // Health check endpoints
                        .requestMatchers("/actuator/health").permitAll()

                        // Read-only endpoints - public access for browsing
                        .requestMatchers("GET", "/api/books/**").permitAll()
                        .requestMatchers("GET", "/api/authors/**").permitAll()
                        .requestMatchers("GET", "/api/genres/**").permitAll()
                        .requestMatchers("GET", "/api/reviews/**").permitAll()
                        .requestMatchers("GET", "/api/ratings/**").permitAll()

                        // Admin only endpoints
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/roles/**").hasRole("ADMIN")
                        .requestMatchers("DELETE", "/api/**").hasRole("ADMIN")

                        // Author and Admin can create/update books, authors, genres
                        .requestMatchers("POST", "/api/books/**").hasAnyRole("ADMIN", "AUTHOR")
                        .requestMatchers("PUT", "/api/books/**").hasAnyRole("ADMIN", "AUTHOR")
                        .requestMatchers("POST", "/api/authors/**").hasAnyRole("ADMIN", "AUTHOR")
                        .requestMatchers("PUT", "/api/authors/**").hasAnyRole("ADMIN", "AUTHOR")
                        .requestMatchers("POST", "/api/genres/**").hasAnyRole("ADMIN", "AUTHOR")
                        .requestMatchers("PUT", "/api/genres/**").hasAnyRole("ADMIN", "AUTHOR")

                        // Authenticated users can create/update reviews and ratings
                        .requestMatchers("POST", "/api/reviews/**").hasAnyRole("ADMIN", "AUTHOR", "USER")
                        .requestMatchers("PUT", "/api/reviews/**").hasAnyRole("ADMIN", "AUTHOR", "USER")
                        .requestMatchers("POST", "/api/ratings/**").hasAnyRole("ADMIN", "AUTHOR", "USER")
                        .requestMatchers("PUT", "/api/ratings/**").hasAnyRole("ADMIN", "AUTHOR", "USER")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                );

        // Add JWT filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        System.out.println("=== JWT Security Configuration Complete ===");
        return http.build();
    }
}
