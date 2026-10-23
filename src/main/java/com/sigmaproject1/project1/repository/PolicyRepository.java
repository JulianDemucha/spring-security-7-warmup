package com.sigmaproject1.project1.repository;

import com.sigmaproject1.project1.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    boolean existsByPolicyNumber(String policyNumber);
}
