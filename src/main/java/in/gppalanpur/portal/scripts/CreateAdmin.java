package in.gppalanpur.portal.scripts;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import in.gppalanpur.portal.entity.User;
import in.gppalanpur.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * Script to create an admin user when the application starts.
 * This will only run when the "dev" profile is active.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class CreateAdmin {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Profile("dev")
    public CommandLineRunner initializeAdmin() {
        return args -> {
            log.info("Checking if admin user exists...");
            
            Optional<User> existingAdmin = userRepository.findByEmail("admin@gppalanpur.in");
            
            if (existingAdmin.isPresent()) {
                log.info("Admin user already exists. Updating roles...");
                User adminUser = existingAdmin.get();
                adminUser.setRoles(new ArrayList<>(Arrays.asList("admin", "student")));
                adminUser.setSelectedRole("admin");
                userRepository.save(adminUser);
                log.info("Admin user updated with roles: {}", adminUser.getRoles());
                return;
            }
            
            log.info("Creating admin user...");
            
            // Create admin user with both admin and student roles
            User adminUser = User.builder()
                .name("Admin")
                .email("admin@gppalanpur.in")
                .password(passwordEncoder.encode("Admin@123"))
                .roles(new ArrayList<>(Arrays.asList("admin", "student"))) // Add both roles to ensure flexibility
                .selectedRole("admin") // Set selected role to match React frontend expectations
                .build();
            
            // Log the roles for debugging
            log.info("Creating admin user with roles: {}", adminUser.getRoles());
            
            userRepository.save(adminUser);
            
            log.info("Admin user created successfully!");
        };
    }
}
