package org.cotato.csquiz.common.websocket;

import org.cotato.csquiz.common.config.jwt.JwtTokenProvider;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.common.error.exception.InterceptorException;
import org.cotato.csquiz.common.error.exception.InterceptorRoleException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.domain.auth.enums.MemberRoleGroup;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@RequiredArgsConstructor
@Slf4j
public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        String[] querySplit = request.getURI().getQuery().split("&");
        String socketToken = null;
        String educationId = null;
        for (String query : querySplit) {
            String[] keyValue = query.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                if (key.equals("Authorization")) {
                    socketToken = value;
                } else if (key.equals("educationId")) {
                    educationId = value;
                }
            }
        }

        try {
            jwtTokenProvider.checkSocketToken(socketToken);
            Long memberId = jwtTokenProvider.getMemberId(socketToken);
            String role = jwtTokenProvider.getRole(socketToken);
            log.info("Connect websocket memberId=" + memberId + ", educationId=" + educationId);
            attributes.put("memberId", memberId);
            attributes.put("role", role);
            attributes.put("educationId", educationId);
            checkRole(role);
        } catch (InterceptorException | ExpiredJwtException | MalformedJwtException | SignatureException exception) {
            log.info("Unauthorized exception occurs");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        } catch (InterceptorRoleException e) {
            log.info("InterceptorRoleException exception occurs");
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

    private void checkRole(String role) {
        MemberRole memberRole = MemberRole.fromKey(role);
        if (!MemberRoleGroup.hasRole(MemberRoleGroup.CLIENTS, memberRole)) {
            throw new InterceptorRoleException("해당 역할은 WS 연결이 불가능합니다.");
        }
    }
}
