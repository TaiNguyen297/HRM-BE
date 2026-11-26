package com.example.hrm_be.service.dto;

import com.example.hrm_be.domain.Department;

import java.time.LocalDateTime;

public record DepartmentResponse(
        Long id,
        String code,
        String name,
        String description,
        Long parentId,
        String parentName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static DepartmentResponse fromEntity(Department dept) {
        Department parent = dept.getParentDepartment();
        return new DepartmentResponse(
                dept.getId(),
                dept.getCode(),
                dept.getName(),
                dept.getDescription(),
                parent != null ? parent.getId() : null,
                parent != null ? parent.getName() : null,
                dept.getCreatedAt(),
                dept.getUpdatedAt()
        );
    }
}
