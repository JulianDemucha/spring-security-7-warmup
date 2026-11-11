package com.sigmaproject1.project1.repository;

import com.sigmaproject1.project1.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Boolean existsByEmail(String email);

    Optional<Customer> findByEmail(String email);

    @Query("""
            SELECT c FROM Customer c
            JOIN User u ON u.customer.id = c.id
            WHERE c.id = :customerId AND u.id = :userId
            """)
    Optional<Customer> findByIdAndUserId(@Param("customerId") Long customerId, @Param("userId") Long userId);
}
