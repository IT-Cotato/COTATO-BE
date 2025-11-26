package org.cotato.csquiz.common.idempotency;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomServletWrappingFilter extends OncePerRequestFilter {

	private static final String EVENT_PATH = "/v2/api/events";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		final ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
		filterChain.doFilter(request, responseWrapper);

		responseWrapper.copyBodyToResponse();
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return request.getRequestURI().startsWith(EVENT_PATH);
	}
}
