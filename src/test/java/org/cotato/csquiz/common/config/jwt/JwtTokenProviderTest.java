package org.cotato.csquiz.common.config.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.cotato.csquiz.domain.auth.constant.TokenConstants;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    private static final String SECRET = "testSecretKey";

    private static final long ACCESS_EXPIRATION = 1_000_000L;

    private static final long REFRESH_EXPIRATION = 2_000_000L;

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private BlackListRepository blackListRepository;

    @Mock
    private MemberRepository memberRepository;


    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "accessExpiration", ACCESS_EXPIRATION);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshExpiration", REFRESH_EXPIRATION);
    }

    @Test
    @DisplayName("JWT 토큰 생성 검증")
    void createToken_ShouldGenerateValidAccessAndRefreshTokens() {
        // given
        Member member = Member.defaultMember("email", "pwd","name", "010");
        ReflectionTestUtils.setField(member, "id", 42L);

        // when
        Token token = jwtTokenProvider.createToken(member);

        // then
        Claims accessClaims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token.getAccessToken())
                .getBody();
        assertEquals(42L, accessClaims.get("id", Long.class));
        assertEquals(TokenConstants.ACCESS_TOKEN, accessClaims.get("type", String.class));
        long accessDuration = accessClaims.getExpiration().getTime() - accessClaims.getIssuedAt().getTime();
        assertEquals(ACCESS_EXPIRATION, accessDuration);

        Claims refreshClaims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token.getRefreshToken())
                .getBody();
        assertEquals(42L, refreshClaims.get("id", Long.class));
        assertEquals(TokenConstants.REFRESH_TOKEN, refreshClaims.get("type", String.class));
        long refreshDuration = refreshClaims.getExpiration().getTime() - refreshClaims.getIssuedAt().getTime();
        assertEquals(REFRESH_EXPIRATION, refreshDuration);
    }
}
