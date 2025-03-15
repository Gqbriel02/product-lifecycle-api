package com.example.productlifecycleapi.service;

import com.example.productlifecycleapi.dto.RegisterRequest;
import com.example.productlifecycleapi.model.AppUser;
import com.example.productlifecycleapi.model.AppRole;
import com.example.productlifecycleapi.repository.AppRoleRepository;
import com.example.productlifecycleapi.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final AppRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AppUserRepository userRepository, AppRoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser registerUser(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Fetch the "User" role from the database
        AppRole userRole = roleRepository.findByRoleName("User")
                .orElseThrow(() -> new RuntimeException("Default 'User' role not found"));

        // Create new user with the default "User" role
        AppUser user = AppUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singleton(userRole))  // Assign "User" role
                .build();

        return userRepository.save(user);
    }
}
