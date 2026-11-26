package com.example.hrm_be.service;

import com.example.hrm_be.service.dto.AttendanceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AttendanceService {

    AttendanceResponse checkIn(String note);

    AttendanceResponse checkOut(String note);

    Page<AttendanceResponse> getMyAttendanceHistory(Pageable pageable);
}
