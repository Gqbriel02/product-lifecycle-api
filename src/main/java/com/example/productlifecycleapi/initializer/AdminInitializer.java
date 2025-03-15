package com.example.productlifecycleapi.initializer;

import com.example.productlifecycleapi.model.AppRole;
import com.example.productlifecycleapi.model.AppUser;
import com.example.productlifecycleapi.repository.AppRoleRepository;
import com.example.productlifecycleapi.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final AppRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(AppUserRepository userRepository, AppRoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        AppRole adminRole = roleRepository.findByRoleName("Admin")
                .orElseGet(() -> roleRepository.save(new AppRole(null, "Admin")));

        Optional<AppUser> existingAdmin = userRepository.findByUsername("admin");

        if (existingAdmin.isEmpty()) {
            AppUser admin = AppUser.builder()
                    .username("admin")
                    .email("admin@plcms.com")
                    .name("Admin User")
                    .phoneNumber("0000000000")
                    .password(passwordEncoder.encode("admin123"))
                    .roles(Collections.singleton(adminRole))
                    .build();

            userRepository.save(admin);
            System.out.println("Admin user created with (username, password): (admin, admin123)");
        } else {
            System.out.println("Admin user already exists. No changes made.");
        }
    }
}
