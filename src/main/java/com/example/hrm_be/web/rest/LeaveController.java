package com.example.hrm_be.web.rest;

import com.example.hrm_be.service.LeaveService;
import com.example.hrm_be.service.dto.LeaveBalanceInitRequest;
import com.example.hrm_be.service.dto.LeaveBalanceResponse;
import com.example.hrm_be.service.dto.LeaveRequestCreate;
import com.example.hrm_be.service.dto.LeaveRequestResponse;
import com.example.hrm_be.service.dto.LeaveReviewRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @GetMapping("/my-balances")
    public ResponseEntity<List<LeaveBalanceResponse>> getMyLeaveBalances() {
        return ResponseEntity.ok(leaveService.getMyLeaveBalances());
    }

    @GetMapping("/my-requests")
    public ResponseEntity<Page<LeaveRequestResponse>> getMyLeaveRequests(Pageable pageable) {
        return ResponseEntity.ok(leaveService.getMyLeaveRequests(pageable));
    }

    @PostMapping
    public ResponseEntity<LeaveRequestResponse> submitLeaveRequest(@Valid @RequestBody LeaveRequestCreate request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveService.submitLeaveRequest(request));
    }

    @PostMapping("/balances")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    public ResponseEntity<LeaveBalanceResponse> initLeaveBalance(@Valid @RequestBody LeaveBalanceInitRequest request) {
        LeaveBalanceResponse created = leaveService.initLeaveBalance(
                request.employeeId(),
                request.year(),
                request.totalDays()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    public ResponseEntity<LeaveRequestResponse> reviewLeaveRequest(
            @PathVariable Long id,
            @Valid @RequestBody LeaveReviewRequest request
    ) {
        return ResponseEntity.ok(leaveService.approveOrRejectLeave(id, request.approved()));
    }
}
