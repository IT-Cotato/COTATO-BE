package org.cotato.csquiz.common.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.common.error.exception.FilterAuthenticationException;
import org.cotato.csquiz.common.error.exception.InterceptorException;
import org.cotato.csquiz.domain.auth.constant.TokenConstants;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secretKey}")
    String secretKey;

    @Value("${jwt.access.expiration}")
    Long accessExpiration;

    @Value("${jwt.refresh.expiration}")
    Long refreshExpiration;

    private static final Long SOCKET_TOKEN_EXPIRATION = 1000 * 30L;
    private final BlackListRepository blackListRepository;
    private final MemberRepository memberRepository;

    public boolean isExpired(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    public String resolveAccessToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new FilterAuthenticationException("Bearer 토큰이 존재하지 않습니다.");
        }
        return getBearer(authorizationHeader);
    }

    public String getBearer(String authorizationHeader) {
        return authorizationHeader.replace("Bearer ", "");
    }

    public String getRole(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return claims.get("role", String.class);
    }

    public String getType(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return claims.get("type", String.class);
    }

    public Token createToken(final Member member) {
        return Token.builder()
                .accessToken(createAccessToken(member.getId()))
                .refreshToken(createRefreshToken(member.getId()))
                .build();
    }

    public void setBlackList(String token) {
        BlackList blackList = BlackList.builder()
                .id(token)
                .ttl(getExpiration(token))
                .build();
        blackListRepository.save(blackList);
    }

    public Long getExpiration(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return claims.getExpiration().getTime() - new Date().getTime();
    }

    private String createAccessToken(final Long id) {
        Claims claims = Jwts.claims();
        claims.put("id", id);
        claims.put("type", TokenConstants.ACCESS_TOKEN);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private String createRefreshToken(final Long id) {
        Claims claims = Jwts.claims();
        claims.put("id", id);
        claims.put("type", TokenConstants.REFRESH_TOKEN);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createSocketToken(Long id, String role) {
        Claims claims = Jwts.claims();
        claims.put("id", id);
        claims.put("role", role);
        claims.put("type", TokenConstants.SOCKET_TOKEN);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + SOCKET_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public void checkSocketToken(String socketToken) {
        String tokenType = getType(socketToken);
        if (!TokenConstants.SOCKET_TOKEN.equals(tokenType)) {
            throw new InterceptorException("소켓 토큰을 이용해주세요.");
        }
    }

    public void validateAccessToken(String accessToken) {
        if (!TokenConstants.ACCESS_TOKEN.equals(getType(accessToken))) {
            throw new FilterAuthenticationException("액세스 토큰을 사용해주세요.");
        }
        if (isBlocked(accessToken)) {
            throw new FilterAuthenticationException("차단된 토큰입니다.");
        }
    }

    private boolean isBlocked(final String token) {
        return blackListRepository.existsById(token);
    }

    public Long getMemberId(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("id", Long.class);
    }

    public Member getMemberByToken(String token) {
        Long memberId = getMemberId(token);
        return memberRepository.findById(memberId).orElseThrow(() -> new FilterAuthenticationException("존재하지 않는 회원입니다."));
    }

    public Optional<Member> getMember(final String token) {
        Long memberId = getMemberId(token);
        return memberRepository.findById(memberId);
    }
}
