package com.sigmaproject1.project1.service;

import com.sigmaproject1.project1.dto.RefreshTokenDto;
import com.sigmaproject1.project1.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CookieFacadeService {
    private final CookieService cookieService;
    private final XsrfTokenService xsrfTokenService;
    private final JwtService jwtService;
    private final UserService userService;

    public void setCookiesAfterAuthentication(
            final HttpServletResponse response,
            final RefreshTokenDto refreshTokenDto
    ) {
        User user  = userService.getById(refreshTokenDto.userId());
        UUID familyId = refreshTokenDto.familyId();

        String jwtToken = jwtService.issueJwtToken(user, familyId);
        String xsrfToken = xsrfTokenService.generateSigned(familyId);
        cookieService.setCookies(response, jwtToken, refreshTokenDto.newRawToken(), xsrfToken);
    }

    public void setCookiesAfterRefresh(
            final HttpServletResponse response,
            final RefreshTokenDto refreshTokenDto
    ) {
        User user  = userService.getById(refreshTokenDto.userId());
        UUID familyId = refreshTokenDto.familyId();

        String jwtToken = jwtService.issueJwtToken(user, familyId);

        if(refreshTokenDto.newRawToken() == null){
            cookieService.setJwtCookie(response, jwtToken);
            return;
        }

        String xsrfToken = xsrfTokenService.generateSigned(familyId);
        cookieService.setCookies(response, jwtToken, refreshTokenDto.newRawToken(), xsrfToken);
    }

    public void clearCookies(final HttpServletResponse response) {
        cookieService.clearCookies(response);
    }

}
