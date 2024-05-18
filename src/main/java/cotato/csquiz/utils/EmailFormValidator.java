package cotato.csquiz.utils;

import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class EmailFormValidator {

    private static final String NAVER_REGEX = "^[a-zA-Z0-9]+@naver\\.com$";
    private static final String GMAIL_REGEX = "^[a-zA-Z0-9]+@gmail\\.com$";
    private static final String ACADEMY_REGEX = "^[a-zA-Z0-9]+@[\\w.-]+\\.(ac\\.kr)$";

    public void validateEmailForm(String email) {
        if (!isNaver(email) && !isGoogle(email) && !isAcademy(email)) {
            throw new AppException(ErrorCode.EMAIL_TYPE_ERROR);
        }
    }

    private boolean isNaver(String email) {
        return email.matches(NAVER_REGEX);
    }

    private boolean isGoogle(String email) {
        return email.matches(GMAIL_REGEX);
    }

    private boolean isAcademy(String email) {
        return email.matches(ACADEMY_REGEX);
    }
}
