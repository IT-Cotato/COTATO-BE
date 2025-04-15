package org.cotato.csquiz.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;

public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^010\\d{8}$");

    private static final String START_NUMBER = "010";


    @Override
    public boolean isValid(String phone, ConstraintValidatorContext constraintValidatorContext) {
        if (phone == null) {
            return false;
        }

        if (phone.startsWith(START_NUMBER) ){
            throw new AppException(ErrorCode.INVALID_PHONE_NUMBER_PREFIX);
        }

        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new AppException(ErrorCode.INVALID_PHONE_NUMBER_FORMAT);
        }

        return true;
    }
}
