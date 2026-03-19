package com.tperons.seed;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.tperons.entity.Permission;
import com.tperons.entity.User;
import com.tperons.repository.PermissionRepository;
import com.tperons.repository.UserRepository;

@Component
public class AdminSeeder implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminSeeder.class);

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_FULL_NAME = "Administrator";

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${seed.admin.password}")
    private String adminPassword;

    public AdminSeeder(UserRepository userRepository, PermissionRepository permissionRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByUserName(ADMIN_USERNAME).isPresent()) {
            logger.info("Admin user already exists, skipping seed.");
            return;
        }

        Permission adminPermission = permissionRepository
                .findByDescription("ADMIN")
                .orElseThrow(() -> new IllegalStateException(
                        "ADMIN permission not found. Make sure migrations ran correctly."));

        User admin = new User(
                ADMIN_USERNAME,
                passwordEncoder.encode(adminPassword),
                ADMIN_FULL_NAME, List.of(adminPermission));

        userRepository.save(admin);
        logger.info("Admin user created successfully. CHANGE THE PASSWORD IMMEDIATELY");
    }

}
