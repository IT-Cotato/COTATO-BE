package org.cotato.csquiz.domain.auth.service;

import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidateService {

	private final MemberRepository memberRepository;

	public void checkDuplicateEmail(String email) {
		if (memberRepository.findByEmail(email).isPresent()) {
			log.error("[회원 가입 실패]: 중복된 이메일 " + email);
			throw new AppException(ErrorCode.EMAIL_DUPLICATED);
		}
	}

	public void checkDuplicatePhoneNumber(String phone) {
		if (memberRepository.findByPhoneNumber(phone).isPresent()) {
			log.error("[회원 가입 실패]: 존재하는 전화번호 " + phone);
			throw new AppException(ErrorCode.PHONE_NUMBER_DUPLICATED);
		}
	}

	public void emailNotExist(String email) {
		if (memberRepository.existsByEmail(email)) {
			throw new AppException(ErrorCode.EMAIL_DUPLICATED);
		}
	}

	public void emailExist(String email) {
		if (!memberRepository.existsByEmail(email)) {
			throw new AppException(ErrorCode.EMAIL_NOT_FOUND);
		}
	}
}
