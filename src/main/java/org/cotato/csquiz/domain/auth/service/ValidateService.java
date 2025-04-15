package org.cotato.csquiz.domain.auth.service;

import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidateService {

    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&.])[A-Za-z\\d@$!%*#?&.]{8,16}$";


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

    public void checkPasswordPattern(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        if (!matcher.matches()) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }
    }
}
