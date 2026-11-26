package com.example.hrm_be.service.impl;

import com.example.hrm_be.domain.Department;
import com.example.hrm_be.domain.Employee;
import com.example.hrm_be.repository.DepartmentRepository;
import com.example.hrm_be.repository.EmployeeRepository;
import com.example.hrm_be.service.EmployeeService;
import com.example.hrm_be.service.dto.EmployeeRequest;
import com.example.hrm_be.service.dto.EmployeeResponse;
import com.example.hrm_be.service.exception.DuplicateResourceException;
import com.example.hrm_be.service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public Page<EmployeeResponse> getAll(Pageable pageable) {
        return employeeRepository.findAll(pageable)
                .map(EmployeeResponse::fromEntity);
    }

    @Override
    public EmployeeResponse getById(Long id) {
        Employee employee = findEmployeeOrThrow(id);
        return EmployeeResponse.fromEntity(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        if (employeeRepository.existsByEmpCode(request.empCode())) {
            throw new DuplicateResourceException("Mã nhân viên đã tồn tại: " + request.empCode());
        }
        if (employeeRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email đã tồn tại: " + request.email());
        }

        Department department = resolveDepartment(request.departmentId());

        Employee employee = Employee.builder()
                .empCode(request.empCode())
                .fullName(request.fullName())
                .email(request.email())
                .department(department)
                .build();

        return EmployeeResponse.fromEntity(employeeRepository.save(employee));
    }

    @Override
    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = findEmployeeOrThrow(id);

        if (!employee.getEmpCode().equals(request.empCode()) && employeeRepository.existsByEmpCode(request.empCode())) {
            throw new DuplicateResourceException("Mã nhân viên đã tồn tại: " + request.empCode());
        }
        if (!employee.getEmail().equals(request.email()) && employeeRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email đã tồn tại: " + request.email());
        }

        Department department = resolveDepartment(request.departmentId());

        employee.setEmpCode(request.empCode());
        employee.setFullName(request.fullName());
        employee.setEmail(request.email());
        employee.setDepartment(department);

        return EmployeeResponse.fromEntity(employeeRepository.save(employee));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Employee employee = findEmployeeOrThrow(id);
        employeeRepository.delete(employee);
    }

    private Employee findEmployeeOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với id: " + id));
    }

    private Department resolveDepartment(Long departmentId) {
        if (departmentId == null) {
            return null;
        }
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng ban với id: " + departmentId));
    }
}
