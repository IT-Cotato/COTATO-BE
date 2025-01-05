package org.cotato.csquiz.common.config;

import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.common.idempotency.IdempotencyRedisRepository;
import org.cotato.csquiz.common.role.RoleInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final IdempotencyRedisRepository idempotencyRedisRepository;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RoleInterceptor())
                .addPathPatterns("/**")
                .order(1);
//        registry.addInterceptor(new IdempotencyInterceptor(idempotencyRedisRepository))
//                .addPathPatterns("/v1/api/record/reply")
//                .order(1);
    }
}
