package com.sigmaproject1.project1.service;

import com.sigmaproject1.project1.config.properties.JwtProperties;
import com.sigmaproject1.project1.dto.RefreshTokenDto;
import com.sigmaproject1.project1.entity.RefreshToken;
import com.sigmaproject1.project1.exception.InvalidRefreshTokenException;
import com.sigmaproject1.project1.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository repo;
    private final JwtProperties jwtProperties;

    // handle newRawToken=null by setting only access token
    @Transactional
    public Optional<RefreshTokenDto> rotate(final String tokenRaw) {
        if (tokenRaw == null)
            throw new InvalidRefreshTokenException();

        RefreshToken rf = repo.findForRotationByTokenHash(hashToken(tokenRaw)) // with lock
                .orElseThrow(
                        () -> new InvalidRefreshTokenException()
                );
        if(rf.getExpiresAt().isBefore(Instant.now()))
            throw new InvalidRefreshTokenException();

        if (rf.getRevokedAt() != null)
            throw new InvalidRefreshTokenException();

        if (rf.getRotatedAt() != null) {
            Instant rotatedAt = rf.getRotatedAt();

            // if token has been rotated longer than 60s it was probably stolen
            if (rotatedAt.isBefore(Instant.now().minusSeconds(60))) {
                revokeFamily(rf.getFamilyId());
                return Optional.empty();
            }

            /*
                [race lost]
                if token has been rotated in the last 60 seconds,
                there was probably few refresh request at the same time
            */
            return Optional.of(
                    new RefreshTokenDto(rf.getUserId(), null, rf.getFamilyId())
            );
        }

        rf.markRotated();

        Long userId = rf.getUserId();
        UUID familyId = rf.getFamilyId();

        return Optional.of(
                new RefreshTokenDto(
                        userId,
                        create(userId, familyId),
                        familyId
                )
        );

    }

    public RefreshTokenDto createNew(final Long userId){
        UUID familyId = UUID.randomUUID();
        return new RefreshTokenDto(
                userId,
                create(userId, familyId),
                familyId
        );
    }

    private String create(final Long userId, final UUID familyId) {
        JwtProperties.CookieProperties c = jwtProperties.refreshCookie();

        String rawToken = UUID.randomUUID().toString();

        repo.save(RefreshToken.builder()
                .expiresAt(Instant.now().plus(c.ttl()))
                .tokenHash(hashToken(rawToken))
                .userId(userId)
                .familyId(familyId)
                .build()
        );

        return rawToken;
    }


    private void revokeFamily(final UUID familyId) {
        repo.revokeAllByFamilyId(familyId, Instant.now());
    }

    @SneakyThrows
    private String hashToken(final String rawToken) {
        byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(rawToken.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(digest);
    }
}
