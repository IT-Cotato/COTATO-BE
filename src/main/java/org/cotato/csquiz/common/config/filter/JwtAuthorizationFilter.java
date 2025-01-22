package org.cotato.csquiz.common.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import jodd.net.HttpMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.common.config.jwt.JwtTokenProvider;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final String AUTH_PATH = "/v1/api/auth/**";
    private static final String LOGIN_PATH = "/login";

    private static final String[] WHITE_LIST = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/favicon.ico",
            "/swagger-ui.html",
            "/v1/api/session/**",
            "/v1/api/generation",
            "/v1/api/generation/current",
            "/websocket/csquiz",
            "/v2/api/policies",
            "/v2/api/events/**",
            "/v2/api/projects/**",
            "/v2/api/random-quizzes/**",
            "/v1/api/education/counts"
    };

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        String accessToken = jwtTokenProvider.resolveAccessToken(authorizationHeader);
        jwtTokenProvider.validateAccessToken(accessToken);

        setAuthentication(accessToken);
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String accessToken) {
        Member member = jwtTokenProvider.getMemberByToken(accessToken);
        String role = member.getRole().toString();
        log.info("authenticated member : <{}> , <{}>", member.getId(), member.getName());

        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(member, "",
                List.of(new SimpleGrantedAuthority(role)));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        log.info("요청 경로 및 메서드: {}, {}", path, request.getMethod());
        return isAuthPath(request.getRequestURI()) || isWhiteList(request);
    }

    private boolean isWhiteList(HttpServletRequest request) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return request.getMethod().equals(HttpMethod.GET.name())
                && Arrays.stream(WHITE_LIST).anyMatch(pattern -> pathMatcher.match(pattern, request.getRequestURI()));
    }

    private boolean isAuthPath(String requestURI) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return pathMatcher.match(AUTH_PATH, requestURI) || pathMatcher.match(LOGIN_PATH, requestURI);
    }
}
