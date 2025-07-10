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

        Roles defaultRole = roleRepo.findByName("ROLE_USER")
                .orElseThrow(() -> new EntityNotFoundException("Default role not found"));
        user.setRole(defaultRole);
        User saved = userRepo.save(user);

        return mapToUserResponse(saved);
    }

    public String login(UserLoginDTO dto) {
        User user = userRepo.findByUsername(dto.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Invalid username"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        // Return a simple token (in real app, use JWT)
        return "token-for-" + user.getUsername();
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
