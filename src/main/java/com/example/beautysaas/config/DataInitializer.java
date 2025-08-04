package com.example.beautysaas.config;

import com.example.beautysaas.entity.Role;
import com.example.beautysaas.entity.User;
import com.example.beautysaas.repository.RoleRepository;
import com.example.beautysaas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constants for role names - using constants helps maintain consistency
    public static final String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing default roles and super admin user");
        initRoles();
        initSuperAdmin();
        log.info("Data initialization completed");
    }

    private void initRoles() {
        // Create Super Admin role if it doesn't exist
        if (roleRepository.findByName(ROLE_SUPER_ADMIN).isEmpty()) {
            Role superAdminRole = Role.builder().name(ROLE_SUPER_ADMIN).build();
            roleRepository.save(superAdminRole);
            log.info("Role created: {}", ROLE_SUPER_ADMIN);
        }

        // Create Admin role if it doesn't exist
        if (roleRepository.findByName(ROLE_ADMIN).isEmpty()) {
            Role adminRole = Role.builder().name(ROLE_ADMIN).build();
            roleRepository.save(adminRole);
            log.info("Role created: {}", ROLE_ADMIN);
        }

        // Create Customer role if it doesn't exist
        if (roleRepository.findByName(ROLE_CUSTOMER).isEmpty()) {
            Role customerRole = Role.builder().name(ROLE_CUSTOMER).build();
            roleRepository.save(customerRole);
            log.info("Role created: {}", ROLE_CUSTOMER);
        }
    }

    private void initSuperAdmin() {
        // Check if super admin already exists with this email
        if (userRepository.findByEmail("superadmin@beautysaas.com").isEmpty()) {
            Role superAdminRole = roleRepository.findByName(ROLE_SUPER_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Super Admin role not found"));

            User superAdmin = User.builder()
                    .name("Super Admin")
                    .email("superadmin@beautysaas.com")
                    .password(passwordEncoder.encode("Admin@123")) // Strong password following policy
                    .role(superAdminRole)
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(superAdmin);
            log.info("Super Admin user created with email: superadmin@beautysaas.com");
        } else {
            log.info("Super Admin user already exists");
        }
    }
}
