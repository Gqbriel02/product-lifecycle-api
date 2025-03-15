package com.example.productlifecycleapi.controller;

import com.example.productlifecycleapi.model.AppRole;
import com.example.productlifecycleapi.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // ✅ Get all roles
    @GetMapping
    public ResponseEntity<List<AppRole>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}
