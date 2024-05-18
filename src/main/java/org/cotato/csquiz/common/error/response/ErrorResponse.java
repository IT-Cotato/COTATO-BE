package org.cotato.csquiz.common.error.response;

import jakarta.servlet.http.HttpServletRequest;
import org.cotato.csquiz.common.error.ErrorCode;

public record ErrorResponse(
        String code,
        String message,
        String method,
        String requestURI
) {

    public static ErrorResponse of(ErrorCode errorCode, HttpServletRequest request) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                request.getMethod(),
                request.getRequestURI()
        );
    }

    public static ErrorResponse of(HttpServletRequest request, ErrorCode errorCode, final String errorMessage) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorMessage,
                request.getMethod(),
                request.getRequestURI()
        );
    }
}
