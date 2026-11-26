package com.example.hrm_be.repository;

import com.example.hrm_be.domain.AttendanceLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Long> {

    @EntityGraph(attributePaths = {"employee"})
    Page<AttendanceLog> findByEmployeeIdOrderByCheckTimeDesc(Long employeeId, Pageable pageable);
}
