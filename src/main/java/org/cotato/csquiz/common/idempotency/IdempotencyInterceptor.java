package org.cotato.csquiz.common.idempotency;

import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotencyInterceptor implements HandlerInterceptor {

	private static final String IDEMPOTENCY_HEADER = "Idempotency-Key";

	private final IdempotencyRedisRepository idempotencyRedisRepository;

	private final ObjectMapper objectMapper;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
		throws Exception {
		String idempotencyKey = request.getHeader(IDEMPOTENCY_HEADER);
		if (idempotencyKey == null) {
			return true;
		}

		if (idempotencyRedisRepository.hasSucceedResult(idempotencyKey)) {
			response.getWriter()
				.write(objectMapper.writeValueAsString(
					idempotencyRedisRepository.getSucceedResponse(idempotencyKey)));
			log.info("[멱등성 DB에 데이터 존재]");
			return false;
		}

		if (idempotencyRedisRepository.isProcessing(idempotencyKey)) {
			response.setStatus(HttpStatus.CONFLICT.value());
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter()
				.write(objectMapper.writeValueAsString(ErrorResponse.of(ErrorCode.PROCESSING, request)));
			log.warn("[요청은 왔지만 아직 처리 중]");
			return false;
		}

		// 캐시에 결과가 존재하지 않으면 -> 처리중이란 값을 넣고 컨트롤러를 실행함
		idempotencyRedisRepository.saveStatusProcessing(idempotencyKey);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
		ModelAndView modelAndView) throws Exception {
		String idempotencyKey = request.getHeader(IDEMPOTENCY_HEADER);
		if (idempotencyKey == null) {
			return;
		}

		final ContentCachingResponseWrapper responseWrapper = (ContentCachingResponseWrapper)response;

		idempotencyRedisRepository.saveSucceedResult(idempotencyKey,
			objectMapper.readTree(responseWrapper.getContentAsByteArray()));

		responseWrapper.copyBodyToResponse();
	}
}
