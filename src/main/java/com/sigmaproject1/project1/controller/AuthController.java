package com.sigmaproject1.project1.controller;

import com.sigmaproject1.project1.dto.RefreshTokenDto;
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

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final CookieFacadeService cookieFacadeService;

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(
            @CookieValue(name = "${app.jwt.refresh-cookie.name}", required = false) final String refreshTokenRaw,
            final HttpServletResponse response
    ) {
        Optional<RefreshTokenDto> rtd = refreshTokenService.rotate(refreshTokenRaw);

        if (rtd.isEmpty()) {
            cookieFacadeService.clearCookies(response);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        cookieFacadeService.setCookiesAfterRefresh(response, rtd.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(
            @RequestBody @Valid final LoginRequest request,
            final HttpServletResponse response
    ) {
        UserDto user = authService.login(request);
        cookieFacadeService.setCookiesAfterAuthentication(
                response,
                refreshTokenService.createNew(user.id())
        );

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "${app.jwt.refresh-cookie.name}", required = false) final String refreshTokenRaw,
            final HttpServletResponse response
    ) {
        refreshTokenService.revokeFamily(refreshTokenRaw);
        cookieFacadeService.clearCookies(response);
         return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
