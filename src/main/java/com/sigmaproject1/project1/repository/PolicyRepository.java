package com.sigmaproject1.project1.repository;

import com.sigmaproject1.project1.entity.Policy;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    boolean existsByPolicyNumber(String policyNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Policy p WHERE p.id = :id")
    Optional<Policy> findByIdForUpdate(@Param("id") long id);

    @Query("""
            SELECT p FROM Policy p
            JOIN User u ON u.customer = p.customer
            WHERE p.id = :policyId AND u.id = :userId
            """)
    Optional<Policy> findByIdAndOwnerUserId(@Param("policyId") Long policyId, @Param("userId") Long userId);

}
