package com.sigmaproject1.project1.repository;

import com.sigmaproject1.project1.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
