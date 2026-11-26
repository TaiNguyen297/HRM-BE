package com.example.hrm_be.service.impl;

import com.example.hrm_be.domain.Department;
import com.example.hrm_be.repository.DepartmentRepository;
import com.example.hrm_be.repository.EmployeeRepository;
import com.example.hrm_be.service.DepartmentService;
import com.example.hrm_be.service.dto.DepartmentRequest;
import com.example.hrm_be.service.dto.DepartmentResponse;
import com.example.hrm_be.service.exception.BusinessException;
import com.example.hrm_be.service.exception.DuplicateResourceException;
import com.example.hrm_be.service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public Page<DepartmentResponse> getAll(Pageable pageable) {
        return departmentRepository.findAll(pageable)
                .map(DepartmentResponse::fromEntity);
    }

    @Override
    @Cacheable(value = "departments", key = "'detail:' + #id")
    public DepartmentResponse getById(Long id) {
        return DepartmentResponse.fromEntity(findDepartmentOrThrow(id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "departments", allEntries = true)
    public DepartmentResponse create(DepartmentRequest request) {
        if (departmentRepository.existsByCode(request.code())) {
            throw new DuplicateResourceException("Mã phòng ban đã tồn tại: " + request.code());
        }

        Department parent = resolveParentDepartment(request.parentId());

        Department department = Department.builder()
                .code(request.code())
                .name(request.name())
                .description(request.description())
                .parentDepartment(parent)
                .build();

        return DepartmentResponse.fromEntity(departmentRepository.save(department));
    }

    @Override
    @Transactional
    @CacheEvict(value = "departments", allEntries = true)
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department department = findDepartmentOrThrow(id);

        if (!department.getCode().equals(request.code()) && departmentRepository.existsByCode(request.code())) {
            throw new DuplicateResourceException("Mã phòng ban đã tồn tại: " + request.code());
        }

        Department parent = null;
        if (request.parentId() != null) {
            if (request.parentId().equals(id)) {
                throw new BusinessException("Phòng ban không thể chọn chính nó làm phòng ban cha");
            }
            parent = resolveParentDepartment(request.parentId());
            ensureNoCycle(id, parent);
        }

        department.setCode(request.code());
        department.setName(request.name());
        department.setDescription(request.description());
        department.setParentDepartment(parent);

        return DepartmentResponse.fromEntity(departmentRepository.save(department));
    }

    @Override
    @Transactional
    @CacheEvict(value = "departments", allEntries = true)
    public void delete(Long id) {
        Department department = findDepartmentOrThrow(id);

        if (employeeRepository.existsByDepartmentId(id)) {
            throw new BusinessException("Không thể xóa phòng ban đang có nhân viên");
        }
        if (departmentRepository.existsByParentDepartmentId(id)) {
            throw new BusinessException("Không thể xóa phòng ban đang có phòng ban con");
        }

        departmentRepository.delete(department);
    }

    private Department findDepartmentOrThrow(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng ban với id: " + id));
    }

    private Department resolveParentDepartment(Long parentId) {
        if (parentId == null) {
            return null;
        }
        return departmentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng ban cha với id: " + parentId));
    }

    private void ensureNoCycle(Long departmentId, Department candidateParent) {
        Department current = candidateParent;
        while (current != null) {
            if (departmentId.equals(current.getId())) {
                throw new BusinessException("Không thể chọn phòng ban con làm phòng ban cha (gây vòng lặp)");
            }
            current = current.getParentDepartment();
        }
    }
}
