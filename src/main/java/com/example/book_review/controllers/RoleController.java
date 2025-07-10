package com.example.book_review.controllers;

import com.example.book_review.dto.*;
import com.example.book_review.services.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Role Management", description = "APIs for managing user roles (Admin only)")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    @Operation(summary = "Get all roles")
    public ResponseEntity<Page<RoleSummaryDTO>> getAllRoles(Pageable pageable) {
        Page<RoleSummaryDTO> roles = roleService.getAllRoles(pageable);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/list")
    @Operation(summary = "Get all roles as list")
    public ResponseEntity<List<RoleSummaryDTO>> getAllRolesList() {
        List<RoleSummaryDTO> roles = roleService.getAllRolesList();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID")
    public ResponseEntity<RoleResponseDTO> getRoleById(@PathVariable Long id) {
        RoleResponseDTO role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @PostMapping
    @Operation(summary = "Create a new role")
    public ResponseEntity<RoleResponseDTO> createRole(@Valid @RequestBody RoleCreateUpdateDTO roleDTO) {
        RoleResponseDTO role = roleService.createRole(roleDTO);
        return new ResponseEntity<>(role, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update role")
    public ResponseEntity<RoleResponseDTO> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleCreateUpdateDTO roleDTO) {
        RoleResponseDTO role = roleService.updateRole(id, roleDTO);
        return ResponseEntity.ok(role);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete role")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search roles by name")
    public ResponseEntity<List<RoleSummaryDTO>> searchRolesByName(@RequestParam String name) {
        List<RoleSummaryDTO> roles = roleService.searchRolesByName(name);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}/users")
    @Operation(summary = "Get users with specific role")
    public ResponseEntity<List<UserSummaryDTO>> getUsersByRole(@PathVariable Long id) {
        List<UserSummaryDTO> users = roleService.getUsersByRole(id);
        return ResponseEntity.ok(users);
    }
}
