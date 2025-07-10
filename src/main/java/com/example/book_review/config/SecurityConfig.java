package com.example.book_review.config;

import com.example.book_review.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("=== Configuring Security Filter Chain ===");

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints - no authentication required
                        .requestMatchers("/api/test/public").permitAll()
                        .requestMatchers("/api/test/test-password").permitAll()
                        .requestMatchers("/api/test/generate-passwords").permitAll()
                        .requestMatchers("/api/test/debug-user/**").permitAll()

                        // Swagger/OpenAPI endpoints
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()

                        // Admin only endpoints
                        .requestMatchers("/api/test/admin").hasRole("ADMIN")
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/roles/**").hasRole("ADMIN")

                        // Author endpoints
                        .requestMatchers("/api/test/author").hasRole("AUTHOR")
                        .requestMatchers("/api/books/**").hasAnyRole("ADMIN", "AUTHOR")
                        .requestMatchers("/api/authors/**").hasAnyRole("ADMIN", "AUTHOR")
                        .requestMatchers("/api/genres/**").hasAnyRole("ADMIN", "AUTHOR")

                        // User endpoints
                        .requestMatchers("/api/test/user").hasRole("USER")
                        .requestMatchers("/api/reviews/**").hasAnyRole("ADMIN", "AUTHOR", "USER")
                        .requestMatchers("/api/ratings/**").hasAnyRole("ADMIN", "AUTHOR", "USER")

                        // All other authenticated endpoints
                        .requestMatchers("/api/test/auth").authenticated()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .userDetailsService(customUserDetailsService);

        System.out.println("=== Security Configuration Complete ===");
        return http.build();
    }
}
