package com.example.hrm_be.service.impl;

import com.example.hrm_be.domain.AttendanceLog;
import com.example.hrm_be.domain.AttendanceType;
import com.example.hrm_be.domain.Employee;
import com.example.hrm_be.repository.AttendanceLogRepository;
import com.example.hrm_be.security.CurrentUserService;
import com.example.hrm_be.service.AttendanceService;
import com.example.hrm_be.service.dto.AttendanceResponse;
import com.example.hrm_be.service.exception.TooManyRequestsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceServiceImpl implements AttendanceService {

    private static final Duration ATTENDANCE_LOCK_TTL = Duration.ofSeconds(10);

    private final AttendanceLogRepository attendanceLogRepository;
    private final CurrentUserService currentUserService;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public AttendanceResponse checkIn(String note) {
        return recordAttendance(AttendanceType.CHECK_IN, note);
    }

    @Override
    @Transactional
    public AttendanceResponse checkOut(String note) {
        return recordAttendance(AttendanceType.CHECK_OUT, note);
    }

    @Override
    public Page<AttendanceResponse> getMyAttendanceHistory(Pageable pageable) {
        Employee employee = currentUserService.getCurrentEmployee();
        return attendanceLogRepository.findByEmployeeIdOrderByCheckTimeDesc(employee.getId(), pageable)
                .map(AttendanceResponse::fromEntity);
    }

    private AttendanceResponse recordAttendance(AttendanceType type, String note) {
        Employee employee = currentUserService.getCurrentEmployee();
        String lockKey = "attendance:lock:" + employee.getId();
        Boolean acquired = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, "LOCKED", ATTENDANCE_LOCK_TTL);

        if (!Boolean.TRUE.equals(acquired)) {
            throw new TooManyRequestsException("Bạn vừa thực hiện chấm công, vui lòng đợi 10 giây trước khi thao tác lại");
        }

        AttendanceLog log = AttendanceLog.builder()
                .employee(employee)
                .checkTime(LocalDateTime.now())
                .type(type)
                .note(note)
                .build();
        return AttendanceResponse.fromEntity(attendanceLogRepository.save(log));
    }
}
