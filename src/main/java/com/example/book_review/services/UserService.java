package com.example.book_review.services;

import com.example.book_review.config.JwtUtil;
import com.example.book_review.dto.*;
import com.example.book_review.models.Roles;
import com.example.book_review.models.User;
import com.example.book_review.repository.RoleRepository;
import com.example.book_review.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired private UserRepository userRepo;
    @Autowired private RoleRepository roleRepo;
    @Autowired private ModelMapper modelMapper;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private AuthenticationManager authenticationManager;

    public UserResponseDTO register(UserRegistrationDTO dto) {
        if (userRepo.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        Roles defaultRole = roleRepo.findByName("ROLE_USER")
                .orElseThrow(() -> new EntityNotFoundException("Default role not found"));
        user.setRole(defaultRole);
        User saved = userRepo.save(user);

        return mapToUserResponse(saved);
    }

    public JwtResponseDTO login(UserLoginDTO dto) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
            );

            // Get user details
            User user = userRepo.findByUsername(dto.getUsername())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            // Generate JWT token
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            return new JwtResponseDTO(token, user.getUsername(), user.getEmail(), user.getRole().getName());

        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("Invalid username or password");
        }
    }

    public UserProfileDTO getUserProfile(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        UserProfileDTO dto = new UserProfileDTO();
        dto.setEmail(user.getEmail());
        return dto;
    }

    public UserProfileDTO updateUserProfile(String username, UserProfileDTO profileDTO) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setEmail(profileDTO.getEmail());
        User updated = userRepo.save(user);

        UserProfileDTO dto = new UserProfileDTO();
        dto.setEmail(updated.getEmail());
        return dto;
    }

    public Page<UserSummaryDTO> getAllUsers(Pageable pageable) {
        Page<User> users = userRepo.findAll(pageable);
        return users.map(user -> new UserSummaryDTO(user.getId(), user.getUsername()));
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userRepo.findById(id.intValue())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return mapToUserResponse(user);
    }

    public void deleteUser(Long id) {
        User user = userRepo.findById(id.intValue())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        userRepo.delete(user);
    }

    public UserResponseDTO updateUserRoles(Long id, RoleCreateUpdateDTO roleDTO) {
        User user = userRepo.findById(id.intValue())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Roles role = roleRepo.findByName(roleDTO.getName())
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        user.setRole(role);
        User updated = userRepo.save(user);
        return mapToUserResponse(updated);
    }

    private UserResponseDTO mapToUserResponse(User user) {
        UserResponseDTO dto = modelMapper.map(user, UserResponseDTO.class);

        RoleSummaryDTO roleDTO = new RoleSummaryDTO(
                user.getRole().getId(),
                user.getRole().getName()
        );
        dto.setRoles(List.of(roleDTO));
        return dto;
    }
}
