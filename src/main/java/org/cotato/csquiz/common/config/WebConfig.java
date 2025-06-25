package org.cotato.csquiz.common.config;

import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.common.idempotency.IdempotencyInterceptor;
import org.cotato.csquiz.common.role.RoleInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RoleInterceptor roleInterceptor;

    private final IdempotencyInterceptor idempotencyInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(roleInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/swagger-ui/**")
                .order(1);
        registry.addInterceptor(idempotencyInterceptor)
                .addPathPatterns("/v1/api/record/reply")
                .order(1);
    }
}
