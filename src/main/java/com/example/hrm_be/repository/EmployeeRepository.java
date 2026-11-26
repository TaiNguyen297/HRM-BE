package com.example.hrm_be.repository;

import com.example.hrm_be.domain.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Override
    @EntityGraph(attributePaths = {"department"})
    Page<Employee> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"department"})
    Optional<Employee> findById(Long id);

    boolean existsByEmail(String email);

    boolean existsByEmpCode(String empCode);

    boolean existsByDepartmentId(Long departmentId);
}
