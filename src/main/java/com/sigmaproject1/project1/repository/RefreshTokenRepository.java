package com.sigmaproject1.project1.repository;

import com.sigmaproject1.project1.entity.RefreshToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RefreshToken> findForRotationByTokenHash(String tokenHash);

    @Modifying
    @Transactional(propagation = Propagation.MANDATORY)
    @Query("""
            UPDATE RefreshToken rt
            SET rt.revokedAt = :now
            WHERE rt.familyId = :familyId
            AND rt.revokedAt IS NULL
            """)
    void revokeAllByFamilyId(@Param("familyId") UUID familyId, @Param("now") Instant now);
}
