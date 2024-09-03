package org.cotato.csquiz.common.config;

import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.common.config.filter.JwtAuthenticationFilter;
import org.cotato.csquiz.common.config.filter.JwtAuthorizationFilter;
import org.cotato.csquiz.common.config.filter.JwtExceptionFilter;
import org.cotato.csquiz.common.config.jwt.JwtTokenProvider;
import org.cotato.csquiz.common.config.jwt.RefreshTokenRepository;
import org.cotato.csquiz.common.error.handler.CustomAccessDeniedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] WHITE_LIST = {
            "/v1/api/auth/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/v1/api/generation",
            "/v1/api/session",
            "/websocket/csquiz",
            "/v2/api/policies",
            "/v2/api/events/**"
    };

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CorsFilter corsFilter;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder sharedObject = http.getSharedObject(AuthenticationManagerBuilder.class);
        AuthenticationManager authenticationManager = sharedObject.build();
        http.authenticationManager(authenticationManager);

        http.cors();
        http.exceptionHandling(exception ->
                exception.accessDeniedHandler(customAccessDeniedHandler));
        http.csrf().disable()
                .formLogin().disable()
                .addFilter(new JwtAuthenticationFilter(authenticationManager, jwtTokenProvider, refreshTokenRepository))
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtExceptionFilter(), JwtAuthorizationFilter.class)
                .addFilter(corsFilter)
                .authorizeHttpRequests(request -> request
                                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                                .requestMatchers("/v1/api/admin/**").hasRole("ADMIN")
                                .requestMatchers(WHITE_LIST).permitAll()
                                .requestMatchers("/v1/api/education/result/**").hasAnyRole("MEMBER", "EDUCATION", "ADMIN")
                                .requestMatchers("/v1/api/education/from").hasAnyRole("MEMBER", "EDUCATION", "ADMIN")
                                .requestMatchers(new AntPathRequestMatcher("/v1/api/education/status", "GET"))
                                .hasAnyRole("MEMBER", "EDUCATION", "ADMIN")
                                .requestMatchers(new AntPathRequestMatcher("/v1/api/education", "GET")).authenticated()
                                .requestMatchers("/v1/api/education/**").hasAnyRole("EDUCATION", "ADMIN")
                                .requestMatchers("/v1/api/generation/**").hasAnyRole("ADMIN")
                                .requestMatchers("/v1/api/mypage/**").hasAnyRole("MEMBER", "OLD_MEMBER", "EDUCATION", "ADMIN")
                                .requestMatchers("/v1/api/quiz/cs-admin/**").hasAnyRole("EDUCATION", "ADMIN")
                                .requestMatchers("/v1/api/quiz/adds").hasAnyRole("EDUCATION", "ADMIN")
                                .requestMatchers("/v1/api/quiz/**").hasAnyRole("MEMBER", "EDUCATION", "ADMIN")
                                .requestMatchers("/v1/api/record/reply").hasAnyRole("MEMBER", "EDUCATION", "ADMIN")
                                .requestMatchers("/v1/api/record/**").hasAnyRole("EDUCATION", "ADMIN")
                                .requestMatchers("/v1/api/session/cs-on").hasAnyRole("EDUCATION", "ADMIN")
                                .requestMatchers(new AntPathRequestMatcher("/v1/api/session", "GET")).authenticated()
                                .requestMatchers("/v1/api/session/**").hasAnyRole("ADMIN")
                                .requestMatchers("/v2/api/attendance/records").hasAnyRole("ADMIN")
                                .requestMatchers("/v2/api/attendance").hasAnyRole("ADMIN")
                                .requestMatchers(new AntPathRequestMatcher("/v1/api/socket/token", "POST"))
                                .hasAnyRole("MEMBER", "EDUCATION", "ADMIN")
//                        .requestMatchers("/v2/api/events/attendances").hasAnyRole("MEMBER", "ADMIN", "EDUCATION")
                                .requestMatchers("/v1/api/socket/**").hasAnyRole("EDUCATION", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/v2/api/projects").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "v2/api/projects/images").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/v2/api/projects/**").permitAll()
                                .anyRequest().authenticated()
                );
        return http.build();
    }
}
