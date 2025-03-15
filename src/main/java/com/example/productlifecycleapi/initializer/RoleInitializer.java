package com.example.productlifecycleapi.initializer;

import com.example.productlifecycleapi.model.AppRole;
import com.example.productlifecycleapi.repository.AppRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class RoleInitializer implements CommandLineRunner {

    private final AppRoleRepository roleRepository;

    public RoleInitializer(AppRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        List<String> roles = Arrays.asList(
                "User", "Admin", "Designer", "Engineer", "Production_Manager",
                "QA_Specialist", "PO", "Portfolio Management", "Seller",
                "Data Analyst"
        );

        roles.forEach(roleName -> {
            if (!roleRepository.existsByRoleName(roleName)) {
                roleRepository.save(new AppRole(null, roleName));
            }
        });
    }
}
