package cotato.csquiz.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InterceptorException extends RuntimeException {

    private String errorMessage;
}
