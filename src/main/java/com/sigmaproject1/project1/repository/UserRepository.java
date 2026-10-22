package com.sigmaproject1.project1.repository;

import com.sigmaproject1.project1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
