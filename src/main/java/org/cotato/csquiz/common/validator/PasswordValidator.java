package org.cotato.csquiz.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&.])[A-Za-z\\d@$!%*#?&.]{8,16}$");

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if (password == null) {
            return false;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        return true;
    }
}
