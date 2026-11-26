package com.example.hrm_be.service.dto;

import com.example.hrm_be.domain.Employee;
import com.example.hrm_be.domain.LeaveRequest;
import com.example.hrm_be.domain.LeaveStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LeaveRequestResponse(
        Long id,
        Long employeeId,
        String employeeName,
        Long approverId,
        String approverName,
        LocalDate startDate,
        LocalDate endDate,
        Double daysRequested,
        String reason,
        LeaveStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static LeaveRequestResponse fromEntity(LeaveRequest req) {
        Employee approver = req.getApprover();
        return new LeaveRequestResponse(
                req.getId(),
                req.getEmployee().getId(),
                req.getEmployee().getFullName(),
                approver != null ? approver.getId() : null,
                approver != null ? approver.getFullName() : null,
                req.getStartDate(),
                req.getEndDate(),
                req.getDaysRequested(),
                req.getReason(),
                req.getStatus(),
                req.getCreatedAt(),
                req.getUpdatedAt()
        );
    }
}
