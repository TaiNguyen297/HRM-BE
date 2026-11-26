package com.example.hrm_be.web.rest;

import com.example.hrm_be.service.AttendanceService;
import com.example.hrm_be.service.dto.AttendanceResponse;
import com.example.hrm_be.service.dto.CheckInRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in")
    public ResponseEntity<AttendanceResponse> checkIn(@RequestBody(required = false) CheckInRequest request) {
        String note = request != null ? request.note() : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.checkIn(note));
    }

    @PostMapping("/check-out")
    public ResponseEntity<AttendanceResponse> checkOut(@RequestBody(required = false) CheckInRequest request) {
        String note = request != null ? request.note() : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.checkOut(note));
    }

    @GetMapping("/my-history")
    public ResponseEntity<Page<AttendanceResponse>> getMyHistory(Pageable pageable) {
        return ResponseEntity.ok(attendanceService.getMyAttendanceHistory(pageable));
    }
}
