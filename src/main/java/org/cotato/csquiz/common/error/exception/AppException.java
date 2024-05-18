package org.cotato.csquiz.common.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.cotato.csquiz.common.error.ErrorCode;

@AllArgsConstructor
@Getter
public class AppException extends RuntimeException {

    private ErrorCode errorCode;
}
