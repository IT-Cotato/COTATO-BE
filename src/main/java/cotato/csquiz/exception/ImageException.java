package cotato.csquiz.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;

@AllArgsConstructor
@Getter
public class ImageException extends IOException {

    private ErrorCode errorCode;
}
