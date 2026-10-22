package com.sigmaproject1.project1.repository;

import com.sigmaproject1.project1.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
}
