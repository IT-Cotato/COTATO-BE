package org.cotato.csquiz.domain.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.cotato.csquiz.common.config.jwt.BlackListRepository;
import org.cotato.csquiz.common.config.jwt.JwtTokenProvider;
import org.cotato.csquiz.common.config.jwt.RefreshToken;
import org.cotato.csquiz.common.config.jwt.RefreshTokenRepository;
import org.cotato.csquiz.common.config.jwt.Token;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@InjectMocks
	private AuthService authService;

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@Mock
	private BlackListRepository blackListRepository;

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@Test
	@DisplayName("리프레시 토큰으로 access, refresh 토큰 재발급 성공")
	void reissue_ShouldReturnNewToken() {
		// given
		final Long memberId = 1L;
		final String oldRefreshToken = "oldRefreshToken";

		Member member = Member.defaultMember("email", "pwd", "name", "010");
		ReflectionTestUtils.setField(member, "id", memberId);

		Token newToken = new Token("newAccessToken", "newRefreshToken");
		RefreshToken refreshTokenEntity = new RefreshToken(memberId, oldRefreshToken);

		when(jwtTokenProvider.isExpired(oldRefreshToken)).thenReturn(false);
		when(blackListRepository.existsById(oldRefreshToken)).thenReturn(false);
		when(jwtTokenProvider.getMember(oldRefreshToken)).thenReturn(Optional.of(member));
		when(refreshTokenRepository.findById(memberId)).thenReturn(Optional.of(refreshTokenEntity));
		when(jwtTokenProvider.createToken(member)).thenReturn(newToken);

		// when
		Token result = authService.reissue(oldRefreshToken);

		// then
		assertEquals("newAccessToken", result.getAccessToken());
		assertEquals("newRefreshToken", result.getRefreshToken());
		verify(jwtTokenProvider).setBlackList(oldRefreshToken);
		verify(refreshTokenRepository).save(refreshTokenEntity);
	}

	@Test
	@DisplayName("리프레시 토큰이 만료된 경우 예외 발생")
	void reissue_ShouldThrowException_WhenTokenIsExpired() {
		// given
		String refreshToken = "expiredToken";
		when(jwtTokenProvider.isExpired(refreshToken)).thenReturn(true);

		// when, then
		assertThrows(AppException.class, () -> authService.reissue(refreshToken));
	}

	@Test
	@DisplayName("리프레시 토큰으로 멤버 조회 실패 시 예외 발생")
	void reissue_ShouldThrowException_WhenMemberNotFound() {
		// given
		String refreshToken = "validButUnknownToken";
		when(jwtTokenProvider.isExpired(refreshToken)).thenReturn(false);
		when(blackListRepository.existsById(refreshToken)).thenReturn(false);
		when(jwtTokenProvider.getMember(refreshToken)).thenReturn(Optional.empty());

		// when & then
		assertThrows(EntityNotFoundException.class, () -> authService.reissue(refreshToken));
	}

	@Test
	@DisplayName("DB에 저장된 리프레시 토큰이 없는 경우 예외 발생")
	void reissue_ShouldThrowException_WhenRefreshTokenNotInDB() {
		// given
		Long memberId = 1L;
		String refreshToken = "validToken";
		Member member = Member.defaultMember("email", "pwd", "name", "010");
		ReflectionTestUtils.setField(member, "id", memberId);

		when(jwtTokenProvider.isExpired(refreshToken)).thenReturn(false);
		when(blackListRepository.existsById(refreshToken)).thenReturn(false);
		when(jwtTokenProvider.getMember(refreshToken)).thenReturn(Optional.of(member));
		when(refreshTokenRepository.findById(memberId)).thenReturn(Optional.empty());

		// when & then
		assertThrows(AppException.class, () -> authService.reissue(refreshToken));
	}

	@Test
	@DisplayName("리프레시 토큰이 DB의 것과 다르면 예외 발생")
	void reissue_ShouldThrowException_WhenTokenMismatch() {
		// given
		Long memberId = 1L;
		String refreshToken = "mismatchedToken";
		String dbToken = "storedToken";
		Member member = Member.defaultMember("email", "pwd", "name", "010");
		ReflectionTestUtils.setField(member, "id", memberId);
		RefreshToken refreshTokenEntity = new RefreshToken(memberId, dbToken);

		when(jwtTokenProvider.isExpired(refreshToken)).thenReturn(false);
		when(blackListRepository.existsById(refreshToken)).thenReturn(false);
		when(jwtTokenProvider.getMember(refreshToken)).thenReturn(Optional.of(member));
		when(refreshTokenRepository.findById(memberId)).thenReturn(Optional.of(refreshTokenEntity));

		// when & then
		assertThrows(AppException.class, () -> authService.reissue(refreshToken));
	}
}
