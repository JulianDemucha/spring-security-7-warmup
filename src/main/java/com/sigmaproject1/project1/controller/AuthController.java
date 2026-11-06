package com.sigmaproject1.project1.controller;

import com.sigmaproject1.project1.dto.UserDto;
import com.sigmaproject1.project1.dto.request.LoginRequest;
import com.sigmaproject1.project1.service.AuthService;
import com.sigmaproject1.project1.service.CookieFacadeService;
import com.sigmaproject1.project1.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final CookieFacadeService cookieFacadeService;


    @PostMapping("/login")
    public ResponseEntity<UserDto> login(
            @RequestBody @Valid LoginRequest request,
            final HttpServletResponse response
    ) {
        UserDto user = authService.login(request);
        cookieFacadeService.setCookiesAfterAuthentication(
                response,
                refreshTokenService.createNew(user.id())
        );

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
