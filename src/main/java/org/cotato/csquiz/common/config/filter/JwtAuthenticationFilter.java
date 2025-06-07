package org.cotato.csquiz.common.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.common.config.auth.PrincipalDetails;
import org.cotato.csquiz.common.config.jwt.JwtTokenProvider;
import org.cotato.csquiz.common.config.jwt.RefreshToken;
import org.cotato.csquiz.common.config.jwt.RefreshTokenRepository;
import org.cotato.csquiz.common.config.jwt.Token;
import org.cotato.csquiz.common.error.exception.FilterAuthenticationException;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.common.util.CookieUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        log.info("[login 요청]");
        ObjectMapper mapper = new ObjectMapper();
        try {
            Member member = mapper.readValue(request.getInputStream(), Member.class);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    member.getEmail(), member.getPassword());
            return authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            log.error("로그인 에러 발생 : <{}>", e.getMessage());
            throw new FilterAuthenticationException("로그인 시도에 실패했습니다.");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) {

        PrincipalDetails principal = (PrincipalDetails) authResult.getPrincipal();

        Token token = jwtTokenProvider.createToken(principal.getMember());

        String accessToken = token.getAccessToken();
        response.addHeader("accessToken", accessToken);

        RefreshToken refreshToken = new RefreshToken(principal.getMember().getId(), token.getRefreshToken());
        refreshToken.updateRefreshToken(token.getRefreshToken());
        refreshTokenRepository.save(refreshToken);

        Cookie cookie = CookieUtil.createRefreshCookie(token.getRefreshToken());

        response.addCookie(cookie);
        log.info("로그인 성공, JWT 토큰 생성");
    }
}
