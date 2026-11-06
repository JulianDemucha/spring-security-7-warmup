package com.sigmaproject1.project1.service;

import com.sigmaproject1.project1.config.properties.JwtProperties;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CookieService {
    private final JwtProperties props;


    public void setCookies(
            final HttpServletResponse response, final String jwtToken,
            final String refreshToken, final String xsrfToken
    ) {
        setCookie(response, refreshCookie(refreshToken));
        setCookie(response, jwtCookie(jwtToken));
        setCookie(response, xsrfCookie(xsrfToken));
    }

    public void setJwtCookie(final HttpServletResponse response, final String jwtToken) {
        setCookie(response, jwtCookie(jwtToken));
    }

    private ResponseCookie createCookie(
            String name, String value, Duration maxAge,
            String path, Boolean httpOnly, String sameSite
    ) {
        return ResponseCookie.from(name, value)
                .maxAge(maxAge)
                .path(path)
                .httpOnly(httpOnly)
                .secure(props.cookieSecure())
                .sameSite(sameSite)
                .build();
    }

    public void clearCookies(final HttpServletResponse response) {
        clearCookie(response, props.accessCookie().name(), props.accessCookie().path());
        clearCookie(response, props.refreshCookie().name(), props.refreshCookie().path());
        clearCookie(response, "XSRF-TOKEN", "/");
    }

    private void clearCookie(
            final HttpServletResponse response,
            final String name, final String path
    ) {
        ResponseCookie cookie = createCookie(name, "", Duration.ZERO, path, true, "Strict");
        setCookie(response, cookie);
    }

    private void setCookie(final HttpServletResponse response, final ResponseCookie cookie) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }


    private ResponseCookie jwtCookie(final String token) {
        JwtProperties.CookieProperties c = props.accessCookie();
        return createCookie(
                c.name(),
                token,
                c.ttl(),
                c.path(),
                c.httpOnly(),
                c.sameSite()
        );
    }

    private ResponseCookie refreshCookie(final String token) {
        JwtProperties.CookieProperties c = props.refreshCookie();
        return createCookie(
                c.name(),
                token,
                c.ttl(),
                c.path(),
                c.httpOnly(),
                c.sameSite()
        );
    }

    private ResponseCookie xsrfCookie(final String token) {
        return createCookie(
                "XSRF-TOKEN",
                token,
                props.refreshCookie().ttl(),
                "/",
                false,
                "Strict"
        );
    }
}
