package com.example.book_review.services;

import com.example.book_review.models.User;
import com.example.book_review.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("=== CustomUserDetailsService.loadUserByUsername() ===");
        System.out.println("Attempting to load user: " + username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("User not found: " + username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        System.out.println("User found successfully:");
        System.out.println("- Username: " + user.getUsername());
        System.out.println("- Email: " + user.getEmail());
        System.out.println("- Role: " + user.getRole().getName());
        System.out.println("- Password hash: " + user.getPassword().substring(0, 20) + "...");

        List<GrantedAuthority> authorities = new ArrayList<>();
        // Add ROLE_ prefix for Spring Security
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));

        System.out.println("- Final role name: ROLE_" + user.getRole().getName());

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();

        System.out.println("UserDetails created successfully with authorities: " + userDetails.getAuthorities());
        System.out.println("=== End CustomUserDetailsService ===");

        return userDetails;
    }
}
