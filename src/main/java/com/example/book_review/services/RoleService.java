package com.example.book_review.services;

import com.example.book_review.dto.*;
import com.example.book_review.models.Roles;
import com.example.book_review.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    public RoleResponseDTO createRole(RoleCreateUpdateDTO dto) {
        if (roleRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Role name already exists");
        }

        Roles role = new Roles();
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());

        Roles savedRole = roleRepository.save(role);
        return mapToRoleResponse(savedRole);
    }

    public Page<RoleSummaryDTO> getAllRoles(Pageable pageable) {
        Page<Roles> roles = roleRepository.findAll(pageable);
        return roles.map(this::mapToRoleSummary);
    }

    public List<RoleSummaryDTO> getAllRolesList() {
        List<Roles> roles = roleRepository.findAll();
        return roles.stream()
                .map(this::mapToRoleSummary)
                .collect(Collectors.toList());
    }

    public RoleResponseDTO getRoleById(Long id) {
        Roles role = roleRepository.findById(id.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
        return mapToRoleResponse(role);
    }

    public RoleResponseDTO updateRole(Long id, RoleCreateUpdateDTO dto) {
        Roles existingRole = roleRepository.findById(id.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));

        if (!existingRole.getName().equals(dto.getName()) &&
                roleRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Role name already exists");
        }

        existingRole.setName(dto.getName());
        existingRole.setDescription(dto.getDescription());

        Roles updatedRole = roleRepository.save(existingRole);
        return mapToRoleResponse(updatedRole);
    }

    public void deleteRole(Long id) {
        Roles role = roleRepository.findById(id.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));

        if (!role.getUsers().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete role. It has " +
                    role.getUsers().size() + " users assigned to it.");
        }

        roleRepository.delete(role);
    }

    public List<RoleSummaryDTO> searchRolesByName(String name) {
        List<Roles> roles = roleRepository.findByNameContainingIgnoreCase(name);
        return roles.stream()
                .map(this::mapToRoleSummary)
                .collect(Collectors.toList());
    }

    public List<UserSummaryDTO> getUsersByRole(Long id) {
        Roles role = roleRepository.findById(id.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        return role.getUsers().stream()
                .map(user -> new UserSummaryDTO(user.getId(), user.getUsername()))
                .collect(Collectors.toList());
    }

    private RoleResponseDTO mapToRoleResponse(Roles role) {
        return modelMapper.map(role, RoleResponseDTO.class);
    }

    private RoleSummaryDTO mapToRoleSummary(Roles role) {
        return new RoleSummaryDTO(role.getId(), role.getName());
    }
}
