package org.cotato.csquiz.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<Phone, String> {

	private static final Pattern PHONE_PATTERN = Pattern.compile("^010\\d{8}$");

	@Override
	public boolean isValid(String phone, ConstraintValidatorContext constraintValidatorContext) {
		if (phone == null) {
			return false;
		}

		return PHONE_PATTERN.matcher(phone).matches();
	}
}
