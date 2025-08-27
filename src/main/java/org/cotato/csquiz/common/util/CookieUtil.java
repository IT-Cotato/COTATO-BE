package org.cotato.csquiz.common.util;

import static org.cotato.csquiz.domain.auth.constant.TokenConstants.REFRESH_TOKEN;

import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class CookieUtil {

    private static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 3;

    public static Cookie createRefreshCookie(final String refreshToken) {
        Cookie refreshCookie = new Cookie(REFRESH_TOKEN, refreshToken);
        refreshCookie.setMaxAge(COOKIE_MAX_AGE);
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);

        log.info("[리프레시 쿠키 발급, 발급시간 : {}]", LocalDateTime.now());

        return refreshCookie;
    }

    public static Cookie getEmptyRefreshCookie() {
        Cookie cookie = new Cookie(REFRESH_TOKEN, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
