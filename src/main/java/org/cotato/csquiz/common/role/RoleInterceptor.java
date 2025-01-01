package org.cotato.csquiz.common.role;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.naming.NoPermissionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        if (!handlerMethod.hasMethodAnnotation(RoleAuthority.class)) {
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Member member = (Member) authentication.getPrincipal();

        RoleAuthority methodAnnotation = handlerMethod.getMethodAnnotation(RoleAuthority.class);
        MemberRole role = methodAnnotation.value();

        if (role.ordinal() > member.getRole().ordinal()) {
            throw new NoPermissionException("cannot process this method with role " + member.getRole());
        }

        return true;
    }
}
