package com.example.productlifecycleapi.service;

import com.example.productlifecycleapi.model.AppRole;
import com.example.productlifecycleapi.repository.AppRoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    private final AppRoleRepository roleRepository;

    public RoleService(AppRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<AppRole> getAllRoles() {
        return roleRepository.findAll();
    }
}
