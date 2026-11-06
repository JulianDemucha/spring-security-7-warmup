package com.sigmaproject1.project1.service;

import com.sigmaproject1.project1.config.properties.JwtProperties;
import com.sigmaproject1.project1.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtEncoder encoder;
    private final JwtProperties props;

    public String issueJwtToken(User user, UUID refreshTokenFamilyId){
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(props.issuer())
                .audience(List.of(props.audience()))
                .issuedAt(now)
                .expiresAt(now.plus(props.accessCookie().ttl()))
                .subject(user.getId().toString())
                .claim("preferred_username", user.getEmail())
                .claim("roles", List.of(user.getRole()))
                .claim("sid", refreshTokenFamilyId.toString())
                .build();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }


}
