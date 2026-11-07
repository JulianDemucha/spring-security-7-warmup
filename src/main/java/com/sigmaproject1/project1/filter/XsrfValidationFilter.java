package com.sigmaproject1.project1.filter;

import com.sigmaproject1.project1.service.XsrfTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.Set;

@RequiredArgsConstructor
public class XsrfValidationFilter extends OncePerRequestFilter {
    private final XsrfTokenService xsrfTokenService;
    private static final Set<String> permittedMethods = Set.of("GET", "HEAD", "OPTIONS", "TRACE");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (permittedMethods.contains(request.getMethod()) || !(authentication instanceof JwtAuthenticationToken jwtAuthToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        Cookie xsrfCookie = WebUtils.getCookie(request, "XSRF-TOKEN");
        if (xsrfCookie == null) {
            response.sendError(403, "Missing XSRF-TOKEN cookie");
            return;
        }

        if (xsrfTokenService.isValid(
                request.getHeader("X-XSRF-TOKEN"),
                xsrfCookie.getValue(),
                jwtAuthToken.getToken().getClaimAsString("sid") //familyId
        )) {
            filterChain.doFilter(request, response);
            return;
        }
        response.sendError(403, "XSRF-TOKEN validation error");


    }


}
