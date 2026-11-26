package com.example.hrm_be.service.impl;

import com.example.hrm_be.domain.Employee;
import com.example.hrm_be.domain.User;
import com.example.hrm_be.repository.EmployeeRepository;
import com.example.hrm_be.repository.UserRepository;
import com.example.hrm_be.security.JwtService;
import com.example.hrm_be.service.AuthService;
import com.example.hrm_be.service.dto.AuthResponse;
import com.example.hrm_be.service.dto.LoginRequest;
import com.example.hrm_be.service.dto.RegisterRequest;
import com.example.hrm_be.service.exception.DuplicateResourceException;
import com.example.hrm_be.service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        return AuthResponse.of(token, user);
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Tên đăng nhập đã tồn tại: " + request.username());
        }

        Employee employee = null;
        if (request.employeeId() != null) {
            if (userRepository.existsByEmployeeId(request.employeeId())) {
                throw new DuplicateResourceException("Nhân viên này đã được liên kết với một tài khoản khác");
            }
            employee = employeeRepository.findById(request.employeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với id: " + request.employeeId()));
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .enabled(true)
                .employee(employee)
                .build();

        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved);
        return AuthResponse.of(token, saved);
    }
}
