package com.example.hrm_be.service.dto;

import jakarta.validation.constraints.NotNull;

public record LeaveReviewRequest(
        @NotNull(message = "Trạng thái duyệt (approved) không được để trống")
        Boolean approved
) {
}
