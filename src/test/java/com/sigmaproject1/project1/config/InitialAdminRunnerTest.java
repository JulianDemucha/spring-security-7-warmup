package com.sigmaproject1.project1.config;

import com.sigmaproject1.project1.config.properties.AdminProperties;
import com.sigmaproject1.project1.entity.User;
import com.sigmaproject1.project1.enums.Role;
import com.sigmaproject1.project1.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitialAdminRunnerTest {
    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private InitialAdminRunner runner(String email, String password) {
        return new InitialAdminRunner(new AdminProperties(email, password), userService, passwordEncoder);
    }

    @Test
    void run_adminDoesNotExist_createsAdmin() {
        when(userService.existsByEmail("admin@test.test")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hash");

        runner(" Admin@Test.test ", "password").run(null);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(captor.capture());
        assertEquals("admin@test.test", captor.getValue().getEmail());
        assertEquals("hash", captor.getValue().getPasswordHash());
        assertEquals(Role.ADMIN, captor.getValue().getRole());
    }

    @Test
    void run_adminAlreadyExists_doesNotCreateAdmin() {
        when(userService.existsByEmail("admin@test.test")).thenReturn(true);

        runner("admin@test.test", "password").run(null);

        verify(userService, never()).save(any());
    }

    @Test
    void run_credentialsNotProvided_doesNotCreateAdmin() {
        runner("", "").run(null);

        verify(userService, never()).save(any());
    }
}
