package com.github.dougmab.openvinylboxapi.config;

import com.github.dougmab.openvinylboxapi.entity.Role;
import com.github.dougmab.openvinylboxapi.entity.User;
import com.github.dougmab.openvinylboxapi.repository.RoleRepository;
import com.github.dougmab.openvinylboxapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Objects;

@Configuration
public class AdminInit implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private Environment env;

    private final Logger logger = LoggerFactory.getLogger(AdminInit.class);

    public AdminInit(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, Environment env) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        String adminPassword = System.getenv("ADMIN_PASSWORD");

        if (Arrays.stream(env.getActiveProfiles()).anyMatch(env -> (env.equalsIgnoreCase("test")))) {
            logger.info("Using test password for admin user creation");
            adminPassword = "admin";
        }

        if (adminPassword == null) {
            throw new RuntimeException("ADMIN_PASSWORD environment variable not set");
        }

        String email = Objects.requireNonNullElse(System.getenv("ADMIN_EMAIL"), "admin@openvinylbox.com");

        if (userRepository.findByEmail(email).isEmpty()) {
            User admin = new User();
            admin.setEmail(email);
            admin.setFirstName("Admin");
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.getRoles().add(roleRepository.findByAuthority(Role.Authorities.ADMIN.name()));

            userRepository.save(admin);

            logger.info("Admin user created");
            return;
        }

        logger.warn("Admin user already exists");
    }
}
