package com.example.hrm_be.service;

import com.example.hrm_be.service.dto.LeaveBalanceResponse;
import com.example.hrm_be.service.dto.LeaveRequestCreate;
import com.example.hrm_be.service.dto.LeaveRequestResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LeaveService {

    LeaveBalanceResponse initLeaveBalance(Long employeeId, Integer year, Double totalDays);

    List<LeaveBalanceResponse> getMyLeaveBalances();

    Page<LeaveRequestResponse> getMyLeaveRequests(Pageable pageable);

    LeaveRequestResponse submitLeaveRequest(LeaveRequestCreate request);

    LeaveRequestResponse approveOrRejectLeave(Long requestId, boolean isApproved);
}
