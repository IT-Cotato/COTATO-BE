package org.cotato.csquiz.common.error.exception;

import java.io.IOException;

import org.cotato.csquiz.common.error.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ImageException extends IOException {

	private ErrorCode errorCode;
}
