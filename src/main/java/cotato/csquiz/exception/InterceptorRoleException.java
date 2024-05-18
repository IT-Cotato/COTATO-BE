package cotato.csquiz.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InterceptorRoleException extends RuntimeException {
    private String errorMessage;
}
