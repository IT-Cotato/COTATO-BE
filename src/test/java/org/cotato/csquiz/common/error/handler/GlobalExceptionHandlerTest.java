package org.cotato.csquiz.common.error.handler;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Objects;

import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.response.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

	@InjectMocks
	private GlobalExceptionHandler globalExceptionHandler;

	@Test
	@DisplayName("예상치 못한 에러 발생 시 예외 처리")
	void handleUnexpectedException() {
		// Given
		Exception exception = new Exception("Unexpected error");
		MockHttpServletRequest request = new MockHttpServletRequest();

		// When
		ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleException(exception,
			request);

		// Then
		assertEquals(500, response.getStatusCode().value());
		assertEquals(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
			Objects.requireNonNull(response.getBody()).message());
	}
}
