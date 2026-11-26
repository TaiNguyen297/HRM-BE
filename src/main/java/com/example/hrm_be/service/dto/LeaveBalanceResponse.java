package com.example.hrm_be.service.dto;

import com.example.hrm_be.domain.LeaveBalance;

public record LeaveBalanceResponse(
        Long id,
        Long employeeId,
        Integer year,
        Double totalDays,
        Double usedDays,
        Double remainingDays
) {
    public static LeaveBalanceResponse fromEntity(LeaveBalance balance) {
        double remaining = balance.getTotalDays() - balance.getUsedDays();
        return new LeaveBalanceResponse(
                balance.getId(),
                balance.getEmployee().getId(),
                balance.getYear(),
                balance.getTotalDays(),
                balance.getUsedDays(),
                remaining
        );
    }
}
