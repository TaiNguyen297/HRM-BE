package com.example.hrm_be.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record LeaveRequestCreate(
        @NotNull(message = "Ngày bắt đầu không được để trống")
        LocalDate startDate,

        @NotNull(message = "Ngày kết thúc không được để trống")
        LocalDate endDate,

        @NotNull(message = "Số ngày xin nghỉ không được để trống")
        @Positive(message = "Số ngày xin nghỉ phải lớn hơn 0")
        Double daysRequested,

        @NotBlank(message = "Lý do nghỉ phép không được để trống")
        String reason
) {
}
