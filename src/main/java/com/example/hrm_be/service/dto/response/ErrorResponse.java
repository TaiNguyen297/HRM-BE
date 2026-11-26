package com.example.hrm_be.service.dto.response;

public record ErrorResponse(Result result) {

    public record Result(
            String responseCode,
            String message,
            boolean ok
    ) {
    }

    public static ErrorResponse of(String responseCode, String message) {
        return new ErrorResponse(new Result(responseCode, message, false));
    }
}
