package com.example.book_review.services;

import com.example.book_review.dto.*;
import com.example.book_review.models.Roles;
import com.example.book_review.models.User;
import com.example.book_review.repository.RoleRepository;
import com.example.book_review.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired private UserRepository userRepo;
    @Autowired private RoleRepository roleRepo;
    @Autowired private ModelMapper modelMapper;
    @Autowired private PasswordEncoder passwordEncoder;

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

        // ✅ Set default role: USER
        Roles defaultRole = roleRepo.findByName("ROLE_USER")
                .orElseThrow(() -> new EntityNotFoundException("Default role not found"));
        user.setRole(defaultRole);
        User saved = userRepo.save(user);

        return mapToUserResponse(saved);
    }

    public UserResponseDTO login(UserLoginDTO dto) {
        User user = userRepo.findByUsername(dto.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Invalid username"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }
        userRepo.save(user); // update login time

        return mapToUserResponse(user);
    }

    // 🔍 PAGINATED USER LIST (admin use)
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        Page<User> users = userRepo.findAll(pageable);
        return users.map(this::mapToUserResponse);
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
