package com.example.hrm_be.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LeaveBalanceInitRequest(
        @NotNull(message = "ID nhân viên không được để trống")
        Long employeeId,

        @NotNull(message = "Năm không được để trống")
        @Min(value = 2000, message = "Năm không hợp lệ")
        Integer year,

        @NotNull(message = "Tổng số ngày phép không được để trống")
        @Positive(message = "Tổng số ngày phép phải lớn hơn 0")
        Double totalDays
) {
}
