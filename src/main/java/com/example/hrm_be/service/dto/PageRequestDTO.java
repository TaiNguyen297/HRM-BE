package com.example.hrm_be.service.dto;

import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public record PageRequestDTO(
        @Min(value = 0, message = "Số trang phải lớn hơn hoặc bằng 0")
        Integer page,

        @Min(value = 1, message = "Kích thước trang phải lớn hơn hoặc bằng 1")
        Integer size
) {
    public Pageable toPageable() {
        int pageNumber = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : 10;
        return PageRequest.of(pageNumber, pageSize);
    }
}
