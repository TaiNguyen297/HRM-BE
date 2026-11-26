package com.example.hrm_be.security;

import com.example.hrm_be.domain.Employee;
import com.example.hrm_be.domain.User;
import com.example.hrm_be.repository.UserRepository;
import com.example.hrm_be.service.exception.BusinessException;
import com.example.hrm_be.service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("Không xác định được người dùng đang đăng nhập");
        }
        String username = authentication.getName();
        return userRepository.findWithEmployeeByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản: " + username));
    }

    public Employee getCurrentEmployee() {
        User user = getCurrentUser();
        if (user.getEmployee() == null) {
            throw new BusinessException("Tài khoản hiện tại chưa được liên kết với hồ sơ nhân viên");
        }
        return user.getEmployee();
    }
}
