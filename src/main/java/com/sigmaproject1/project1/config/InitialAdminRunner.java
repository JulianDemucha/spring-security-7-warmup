package com.sigmaproject1.project1.config;

import com.sigmaproject1.project1.config.properties.AdminProperties;
import com.sigmaproject1.project1.entity.User;
import com.sigmaproject1.project1.enums.Role;
import com.sigmaproject1.project1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.sigmaproject1.project1.utils.EmailUtils.normalizeEmail;

@Component
@RequiredArgsConstructor
public class InitialAdminRunner implements ApplicationRunner {
    private final AdminProperties adminProperties;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (!StringUtils.hasText(adminProperties.email()) || !StringUtils.hasText(adminProperties.password()))
            return;

        String email = normalizeEmail(adminProperties.email());

        if (userService.existsByEmail(email))
            return;

        userService.save(
                User.builder()
                        .email(email)
                        .passwordHash(passwordEncoder.encode(adminProperties.password()))
                        .role(Role.ADMIN)
                        .build()
        );
    }
}
