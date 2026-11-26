package com.example.hrm_be.service.dto;

import com.example.hrm_be.domain.AttendanceLog;
import com.example.hrm_be.domain.AttendanceType;

import java.time.LocalDateTime;

public record AttendanceResponse(
        Long id,
        Long employeeId,
        String employeeName,
        LocalDateTime checkTime,
        AttendanceType type,
        String note
) {
    public static AttendanceResponse fromEntity(AttendanceLog log) {
        return new AttendanceResponse(
                log.getId(),
                log.getEmployee().getId(),
                log.getEmployee().getFullName(),
                log.getCheckTime(),
                log.getType(),
                log.getNote()
        );
    }
}
