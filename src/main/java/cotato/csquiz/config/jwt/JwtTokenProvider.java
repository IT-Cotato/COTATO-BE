package cotato.csquiz.config.jwt;

import cotato.csquiz.domain.constant.TokenConstants;
import cotato.csquiz.exception.FilterAuthenticationException;
import cotato.csquiz.exception.InterceptorException;
import cotato.csquiz.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    public Token createToken(Long id, String authority) {
        return Token.builder()
                .accessToken(createAccessToken(id, authority))
                .refreshToken(createRefreshToken(id, authority))
                .build();
    }

    @Transactional
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

    private String createAccessToken(Long id, String authority) {
        Claims claims = Jwts.claims();
        claims.put("id", id);
        claims.put("role", authority);
        claims.put("type", TokenConstants.ACCESS_TOKEN);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private String createRefreshToken(Long id, String authority) {
        Claims claims = Jwts.claims();
        claims.put("id", id);
        claims.put("role", authority);
        claims.put("type", TokenConstants.REFRESH_TOKEN);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public void checkMemberExist(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new FilterAuthenticationException("존재하지 않는 회원입니다.");
        }
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
}
