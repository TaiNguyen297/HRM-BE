package com.example.hrm_be.service.dto;

import jakarta.validation.constraints.NotBlank;

public record DepartmentRequest(
        @NotBlank(message = "Mã phòng ban không được để trống")
        String code,

        @NotBlank(message = "Tên phòng ban không được để trống")
        String name,

        String description,

        Long parentId
) {
}
