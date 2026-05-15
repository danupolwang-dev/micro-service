package com.example.user_service.config;

import com.example.user_service.model.Roles;
import com.example.user_service.model.User;
import com.example.user_service.model.UserRoles;
import com.example.user_service.repository.RolesRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.repository.UserRolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order; // Import Order
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(1) // Force this seeder to run FIRST
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private UserRolesRepository userRolesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Create "ADMIN" and "USER" roles if they don't exist
        Roles adminRole = rolesRepository.findByName("ADMIN").orElseGet(() -> {
            Roles newRole = new Roles();
            newRole.setName("ADMIN");
            return rolesRepository.save(newRole);
        });

        rolesRepository.findByName("USER").orElseGet(() -> {
            Roles newRole = new Roles();
            newRole.setName("USER");
            return rolesRepository.save(newRole);
        });

        // 2. Create "admin" user if it doesn't exist
        if (userRepository.findByUserName("admin").isEmpty()) {
            User admin = new User();
            admin.setUserName("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setMobileNo("0000000000");
            User savedAdmin = userRepository.save(admin);

            // 3. Assign ADMIN role to the admin user
            UserRoles userRoles = new UserRoles(savedAdmin, adminRole);
            userRolesRepository.save(userRoles);

            System.out.println("Admin user and roles created in user-service.");
        }
    }
}
