package org.cotato.csquiz.common.validator;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {

	private static final Pattern PASSWORD_PATTERN = Pattern.compile(
		"^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&.])[A-Za-z\\d@$!%*#?&.]{8,16}$");

	@Override
	public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
		if (password == null) {
			return false;
		}

		return PASSWORD_PATTERN.matcher(password).matches();
	}
}
