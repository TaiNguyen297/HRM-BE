package com.example.hrm_be.service;

import com.example.hrm_be.service.dto.DepartmentRequest;
import com.example.hrm_be.service.dto.DepartmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepartmentService {

    Page<DepartmentResponse> getAll(Pageable pageable);

    DepartmentResponse getById(Long id);

    DepartmentResponse create(DepartmentRequest request);

    DepartmentResponse update(Long id, DepartmentRequest request);

    void delete(Long id);
}
