package com.example.hrm_be.repository;

import com.example.hrm_be.domain.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @Override
    @EntityGraph(attributePaths = {"parentDepartment"})
    Page<Department> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"parentDepartment"})
    Optional<Department> findById(Long id);

    boolean existsByCode(String code);

    boolean existsByParentDepartmentId(Long parentId);
}
