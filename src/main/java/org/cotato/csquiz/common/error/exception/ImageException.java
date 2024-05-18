package org.cotato.csquiz.common.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import org.cotato.csquiz.common.error.ErrorCode;

@AllArgsConstructor
@Getter
public class ImageException extends IOException {

    private ErrorCode errorCode;
}
