package org.cotato.csquiz.common.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import jodd.net.HttpMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.common.config.jwt.JwtTokenProvider;
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

    private static final String AUTH_PATH = "/v1/api/auth";
    private static final String LOGIN_PATH = "/login";
    private static final String SWAGGER_PATH = "/swagger-ui";
    private static final String SWAGGER_PATH_3 = "/v3/api-docs";
    private static final String SWAGGER_FAVICON = "/favicon.ico";
    private static final String WS = "/websocket/csquiz";
    private static final String GENERATION_PATH = "/v1/api/generation";
    private static final String SESSION_PATH = "/v1/api/session";
    private static final String POLICIES_PATH = "/v2/api/policies";
    private static final String PROJECTS_LIST = "/v2/api/projects";
    private static final String PROJECT_DETAIL = "/v2/api/projects";
    private static final String CURRENT_GENERATION = "/v1/api/generation/current";
    private static final String RANDOM_QUIZ_PATH = "/v3/api/random-quiz";
    private static final String INTEGER_REGEX = "/{id:\\d+}";

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
        Long memberId = jwtTokenProvider.getMemberId(accessToken);
        String role = jwtTokenProvider.getRole(accessToken);
        log.info("[인증 필터 인증 진행, {}]", memberId);
        log.info("Member Role: {}", role);

        jwtTokenProvider.checkMemberExist(memberId);

        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(memberId, "",
                List.of(new SimpleGrantedAuthority(role)));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        log.info("요청 경로 및 메서드: {}, {}", path, request.getMethod());
        return path.startsWith(AUTH_PATH) || path.equals(LOGIN_PATH)
                || path.startsWith(SWAGGER_PATH) || path.equals(SWAGGER_FAVICON)
                || path.startsWith(SWAGGER_PATH_3) || path.startsWith(WS)
                || path.startsWith(RANDOM_QUIZ_PATH)
                || path.equals(GENERATION_PATH) || path.equals(SESSION_PATH)
                || path.equals(POLICIES_PATH)
                || path.equals(CURRENT_GENERATION)
                || path.equals(PROJECTS_LIST) && HttpMethod.GET.name().equals(request.getMethod())
                || new AntPathMatcher().match(PROJECT_DETAIL + INTEGER_REGEX, path)
                || (new AntPathMatcher().match(SESSION_PATH + INTEGER_REGEX, path) && request.getMethod().equals(HttpMethod.GET.name()))
                ;
    }
}
