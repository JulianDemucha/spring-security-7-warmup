package com.sigmaproject1.project1.service;

import com.sigmaproject1.project1.dto.UserDto;
import com.sigmaproject1.project1.dto.request.LoginRequest;
import com.sigmaproject1.project1.entity.User;
import com.sigmaproject1.project1.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

import static com.sigmaproject1.project1.utils.EmailUtils.normalizeEmail;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserDto login(final LoginRequest request) {
        String email = normalizeEmail(request.email());
        Optional<User> userOpt = userService.findByEmail(email);

        if (userOpt.isEmpty())
            throw new InvalidCredentialsException();

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash()))
            throw new InvalidCredentialsException();

        return UserDto.from(user);
    }
}
