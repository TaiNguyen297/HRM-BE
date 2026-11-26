package com.example.hrm_be.service.impl;

import com.example.hrm_be.domain.Employee;
import com.example.hrm_be.domain.LeaveBalance;
import com.example.hrm_be.domain.LeaveRequest;
import com.example.hrm_be.domain.LeaveStatus;
import com.example.hrm_be.domain.User;
import com.example.hrm_be.repository.EmployeeRepository;
import com.example.hrm_be.repository.LeaveBalanceRepository;
import com.example.hrm_be.repository.LeaveRequestRepository;
import com.example.hrm_be.security.CurrentUserService;
import com.example.hrm_be.service.LeaveService;
import com.example.hrm_be.service.dto.LeaveBalanceResponse;
import com.example.hrm_be.service.dto.LeaveRequestCreate;
import com.example.hrm_be.service.dto.LeaveRequestResponse;
import com.example.hrm_be.service.exception.BusinessException;
import com.example.hrm_be.service.exception.DuplicateResourceException;
import com.example.hrm_be.service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaveServiceImpl implements LeaveService {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public LeaveBalanceResponse initLeaveBalance(Long employeeId, Integer year, Double totalDays) {
        if (leaveBalanceRepository.existsByEmployeeIdAndYear(employeeId, year)) {
            throw new DuplicateResourceException("Quỹ phép năm " + year + " của nhân viên " + employeeId + " đã tồn tại");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với id: " + employeeId));

        LeaveBalance balance = LeaveBalance.builder()
                .employee(employee)
                .year(year)
                .totalDays(totalDays != null ? totalDays : 12.0)
                .usedDays(0.0)
                .build();

        return LeaveBalanceResponse.fromEntity(leaveBalanceRepository.save(balance));
    }

    @Override
    public List<LeaveBalanceResponse> getMyLeaveBalances() {
        Employee employee = currentUserService.getCurrentEmployee();
        return leaveBalanceRepository.findByEmployeeIdOrderByYearDesc(employee.getId())
                .stream()
                .map(LeaveBalanceResponse::fromEntity)
                .toList();
    }

    @Override
    public Page<LeaveRequestResponse> getMyLeaveRequests(Pageable pageable) {
        Employee employee = currentUserService.getCurrentEmployee();
        return leaveRequestRepository.findByEmployeeIdOrderByCreatedAtDesc(employee.getId(), pageable)
                .map(LeaveRequestResponse::fromEntity);
    }

    @Override
    @Transactional
    public LeaveRequestResponse submitLeaveRequest(LeaveRequestCreate request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new BusinessException("Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu");
        }

        Employee employee = currentUserService.getCurrentEmployee();
        int year = request.startDate().getYear();

        LeaveBalance balance = leaveBalanceRepository.findByEmployeeIdAndYear(employee.getId(), year)
                .orElseThrow(() -> new ResourceNotFoundException("Chưa khởi tạo quỹ phép năm " + year + " cho nhân viên"));

        double remaining = balance.getTotalDays() - balance.getUsedDays();
        if (remaining < request.daysRequested()) {
            throw new BusinessException("Số ngày phép còn lại không đủ (còn lại: " + remaining + " ngày)");
        }

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .employee(employee)
                .startDate(request.startDate())
                .endDate(request.endDate())
                .daysRequested(request.daysRequested())
                .reason(request.reason())
                .status(LeaveStatus.PENDING)
                .build();

        return LeaveRequestResponse.fromEntity(leaveRequestRepository.save(leaveRequest));
    }

    @Override
    @Transactional
    public LeaveRequestResponse approveOrRejectLeave(Long requestId, boolean isApproved) {
        LeaveRequest leaveRequest = leaveRequestRepository.findWithDetailsById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn nghỉ phép với id: " + requestId));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new BusinessException("Chỉ có thể xử lý đơn nghỉ phép đang ở trạng thái PENDING");
        }

        User reviewer = currentUserService.getCurrentUser();
        leaveRequest.setApprover(reviewer.getEmployee());

        if (isApproved) {
            int year = leaveRequest.getStartDate().getYear();
            LeaveBalance balance = leaveBalanceRepository.findByEmployeeIdAndYear(leaveRequest.getEmployee().getId(), year)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy quỹ phép năm " + year + " của nhân viên"));

            double remaining = balance.getTotalDays() - balance.getUsedDays();
            if (remaining < leaveRequest.getDaysRequested()) {
                throw new BusinessException("Số ngày phép còn lại không đủ để duyệt đơn (còn lại: " + remaining + " ngày)");
            }

            balance.setUsedDays(balance.getUsedDays() + leaveRequest.getDaysRequested());
            leaveBalanceRepository.save(balance);
            leaveRequest.setStatus(LeaveStatus.APPROVED);
        } else {
            leaveRequest.setStatus(LeaveStatus.REJECTED);
        }

        return LeaveRequestResponse.fromEntity(leaveRequestRepository.save(leaveRequest));
    }
}
