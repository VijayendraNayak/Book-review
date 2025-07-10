package com.example.book_review.services;

import com.example.book_review.dto.RoleCreateUpdateDTO;
import com.example.book_review.dto.RoleResponseDTO;
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

    // ✅ CREATE ROLE
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

    // 🔍 GET ALL ROLES (with pagination)
    public Page<RoleResponseDTO> getAllRoles(Pageable pageable) {
        Page<Roles> roles = roleRepository.findAll(pageable);
        return roles.map(this::mapToRoleResponse);
    }

    // 🔍 GET ALL ROLES (without pagination)
    public List<RoleResponseDTO> getAllRoles() {
        List<Roles> roles = roleRepository.findAll();
        return roles.stream()
                .map(this::mapToRoleResponse)
                .collect(Collectors.toList());
    }

    // 🔍 GET ROLE BY ID
    public RoleResponseDTO getRoleById(int id) {
        Roles role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
        return mapToRoleResponse(role);
    }

    // 🔍 GET ROLE BY NAME
    public RoleResponseDTO getRoleByName(String name) {
        Roles role = roleRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with name: " + name));
        return mapToRoleResponse(role);
    }

    // ✏️ UPDATE ROLE
    public RoleResponseDTO updateRole(int id, RoleCreateUpdateDTO dto) {
        Roles existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));

        // Check if the new name already exists (but not for the current role)
        if (!existingRole.getName().equals(dto.getName()) &&
                roleRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Role name already exists");
        }

        existingRole.setName(dto.getName());
        existingRole.setDescription(dto.getDescription());

        Roles updatedRole = roleRepository.save(existingRole);
        return mapToRoleResponse(updatedRole);
    }

    // 🗑️ DELETE ROLE
    public void deleteRole(int id) {
        Roles role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));

        // Check if role has users assigned
        if (!role.getUsers().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete role. It has " +
                    role.getUsers().size() + " users assigned to it.");
        }

        roleRepository.delete(role);
    }

    // 🔍 CHECK IF ROLE EXISTS
    public boolean existsById(int id) {
        return roleRepository.existsById(id);
    }

    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }

    // 📊 GET ROLE COUNT
    public long getRoleCount() {
        return roleRepository.count();
    }

    // 🔄 HELPER METHOD: Map Entity to Response DTO
    private RoleResponseDTO mapToRoleResponse(Roles role) {
        return modelMapper.map(role, RoleResponseDTO.class);
    }
}