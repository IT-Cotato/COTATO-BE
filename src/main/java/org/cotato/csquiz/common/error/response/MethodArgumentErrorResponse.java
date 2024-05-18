package org.cotato.csquiz.common.error.response;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.common.error.ErrorCode;
import org.springframework.validation.FieldError;

public record MethodArgumentErrorResponse(
        String code,
        String message,
        String method,
        String requestURI,
        List<FieldErrorResponse> errors
) {

    public static MethodArgumentErrorResponse of(ErrorCode errorCode, HttpServletRequest request,
                                                 List<FieldErrorResponse> errors) {
        return new MethodArgumentErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                request.getMethod(),
                request.getRequestURI(),
                errors
        );
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FieldErrorResponse {
        private String field;
        private String reason;

        public static FieldErrorResponse of(FieldError fieldError) {
            return new FieldErrorResponse(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }
    }
}
