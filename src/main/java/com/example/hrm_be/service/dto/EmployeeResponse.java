package com.example.hrm_be.service.dto;

import com.example.hrm_be.domain.Department;
import com.example.hrm_be.domain.Employee;

import java.time.LocalDateTime;

public record EmployeeResponse(
        Long id,
        String empCode,
        String fullName,
        String email,
        DepartmentSummary department,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public record DepartmentSummary(Long id, String code, String name) {
    }

    public static EmployeeResponse fromEntity(Employee emp) {
        Department dept = emp.getDepartment();
        DepartmentSummary deptSummary = dept != null
                ? new DepartmentSummary(dept.getId(), dept.getCode(), dept.getName())
                : null;

        return new EmployeeResponse(
                emp.getId(),
                emp.getEmpCode(),
                emp.getFullName(),
                emp.getEmail(),
                deptSummary,
                emp.getCreatedAt(),
                emp.getUpdatedAt()
        );
    }
}
