package com.example.hrm_be.repository;

import com.example.hrm_be.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = {"employee"})
    Optional<User> findWithEmployeeByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmployeeId(Long employeeId);
}
