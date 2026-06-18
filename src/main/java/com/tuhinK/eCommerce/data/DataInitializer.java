package com.tuhinK.eCommerce.data;

import com.tuhinK.eCommerce.user.models.Role;
import com.tuhinK.eCommerce.user.models.User;
import com.tuhinK.eCommerce.user.repositories.UserRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Transactional
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String DEFAULT_PASSWORD = "123456";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(@Nonnull ApplicationReadyEvent event) {

        ensureRolesExist(Set.of(ROLE_ADMIN, ROLE_USER));

        Role userRole = getRole(ROLE_USER);
        Role adminRole = getRole(ROLE_ADMIN);

        createDefaultUsers(userRole);
        createDefaultAdmins(adminRole);
    }

    private void ensureRolesExist(Set<String> roleNames) {
        roleNames.stream()
                .filter(roleName -> roleRepository.findByName(roleName).isEmpty())
                .map(Role::new)
                .forEach(roleRepository::save);
    }

    private Role getRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() ->
                        new IllegalStateException("Role not found: " + roleName));
    }

    private void createDefaultUsers(Role userRole) {
        for (int i = 1; i <= 5; i++) {
            createUserIfNotExists(
                    "user" + i + "@email.com",
                    "The User",
                    "User" + i,
                    userRole
            );
        }
    }

    private void createDefaultAdmins(Role adminRole) {
        for (int i = 1; i <= 2; i++) {
            createUserIfNotExists(
                    "admin" + i + "@email.com",
                    "Admin",
                    "Admin" + i,
                    adminRole
            );
        }
    }

    private void createUserIfNotExists(
            String email,
            String firstName,
            String lastName,
            Role role
    ) {

        if (userRepository.existsByEmail(email)) {
            return;
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        user.setRoles(Set.of(role));

        userRepository.save(user);

        log.info("Created default user with email: {}", email);
    }
}
