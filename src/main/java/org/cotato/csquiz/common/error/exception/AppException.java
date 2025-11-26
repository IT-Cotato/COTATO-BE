package org.cotato.csquiz.common.error.exception;

import org.cotato.csquiz.common.error.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AppException extends RuntimeException {

	private ErrorCode errorCode;
}
