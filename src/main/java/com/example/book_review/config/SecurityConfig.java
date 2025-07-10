package com.example.book_review.config;

import com.example.book_review.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/users/register", "/api/users/login").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        // Book endpoints - reading is public, management requires AUTHOR or ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/books/**").hasAnyRole("AUTHOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/books/**").hasAnyRole("AUTHOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasAnyRole("AUTHOR", "ADMIN")

                        // Author endpoints
                        .requestMatchers(HttpMethod.GET, "/api/authors/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/authors/**").hasAnyRole("AUTHOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/authors/**").hasAnyRole("AUTHOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/authors/**").hasAnyRole("AUTHOR", "ADMIN")

                        // Genre endpoints
                        .requestMatchers(HttpMethod.GET, "/api/genres/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/genres/**").hasAnyRole("AUTHOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/genres/**").hasAnyRole("AUTHOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/genres/**").hasAnyRole("AUTHOR", "ADMIN")

                        // Review endpoints
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/reviews/**").hasAnyRole("USER", "AUTHOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/reviews/**").hasAnyRole("USER", "AUTHOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/reviews/**").hasAnyRole("USER", "AUTHOR", "ADMIN")

                        // Rating endpoints
                        .requestMatchers(HttpMethod.GET, "/api/ratings/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/ratings/**").hasAnyRole("USER", "AUTHOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/ratings/**").hasAnyRole("USER", "AUTHOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/ratings/**").hasAnyRole("USER", "AUTHOR", "ADMIN")

                        // User management
                        .requestMatchers(HttpMethod.GET, "/api/users/profile").hasAnyRole("USER", "AUTHOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/profile").hasAnyRole("USER", "AUTHOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")

                        // Role management - only ADMIN
                        .requestMatchers("/api/roles/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
