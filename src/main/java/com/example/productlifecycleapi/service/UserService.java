package com.example.productlifecycleapi.service;

import com.example.productlifecycleapi.model.AppUser;
import com.example.productlifecycleapi.model.AppRole;
import com.example.productlifecycleapi.repository.AppUserRepository;
import com.example.productlifecycleapi.repository.AppRoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final AppUserRepository userRepository;
    private final AppRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository userRepository, AppRoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<AppUser> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<AppUser> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public AppUser createUser(AppUser user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            AppRole userRole = roleRepository.findByRoleName("User")
                    .orElseThrow(() -> new RuntimeException("Default role 'User' not found"));
            user.setRoles(Collections.singleton(userRole));
        } else {
            Set<AppRole> validatedRoles = user.getRoles().stream()
                    .map(role -> roleRepository.findById(role.getId()).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            user.setRoles(validatedRoles);
        }

        return userRepository.save(user);
    }

    public AppUser updateUser(Long id, AppUser userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(userDetails.getUsername());
                    user.setEmail(userDetails.getEmail());
                    user.setPhoneNumber(userDetails.getPhoneNumber());

                    if (userDetails.getPassword() != null && !userDetails.getPassword().isBlank()) {
                        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                    }

                    if (userDetails.getRoles() != null) {
                        Set<AppRole> existingRoles = user.getRoles();
                        Set<AppRole> newRoles = userDetails.getRoles().stream()
                                .map(role -> roleRepository.findById(role.getId()).orElse(null))
                                .filter(Objects::nonNull)
                                .collect(Collectors.toSet());

                        existingRoles.clear();
                        existingRoles.addAll(newRoles);
                        user.setRoles(existingRoles);
                    }

                    return userRepository.save(user);
                }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
}
