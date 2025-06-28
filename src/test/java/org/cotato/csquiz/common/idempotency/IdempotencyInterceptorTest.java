package org.cotato.csquiz.common.idempotency;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IdempotencyInterceptorTest {

    @Mock
    private IdempotencyRedisRepository idempotencyRedisRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private IdempotencyInterceptor interceptor;

    @Test
    void testPreHandle_NoIdempotencyKey() throws Exception {
        // Given
        when(request.getHeader("Idempotency-Key")).thenReturn(null);

        // When
        boolean result = interceptor.preHandle(request, response, new Object());

        // Then
        assertTrue(result);
        verifyNoInteractions(idempotencyRedisRepository);
    }

    @Test
    void testPreHandle_SucceedResultExists() throws Exception {
        // Given
        String idempotencyKey = "test-key";
        when(request.getHeader("Idempotency-Key")).thenReturn(idempotencyKey);
        when(idempotencyRedisRepository.hasSucceedResult(idempotencyKey)).thenReturn(true);
        when(idempotencyRedisRepository.getSucceedResponse(idempotencyKey)).thenReturn("Success Response");

        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);
        when(objectMapper.writeValueAsString("Success Response")).thenReturn("\"Success Response\"");

        // When
        boolean result = interceptor.preHandle(request, response, new Object());

        // Then
        assertFalse(result);
        verify(response).getWriter();
        verify(writer).write("\"Success Response\"");
        verify(idempotencyRedisRepository).hasSucceedResult(idempotencyKey);
        verify(idempotencyRedisRepository).getSucceedResponse(idempotencyKey);
    }

    @Test
    void testPreHandle_Processing() throws Exception {
        // Given
        String idempotencyKey = "test-key";
        when(request.getHeader("Idempotency-Key")).thenReturn(idempotencyKey);
        when(idempotencyRedisRepository.isProcessing(idempotencyKey)).thenReturn(true);

        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"code\":\"PROCESSING\"}");

        // When
        boolean result = interceptor.preHandle(request, response, new Object());

        // Then
        assertFalse(result);
        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        verify(response).setContentType("application/json; charset=UTF-8");
        verify(writer).write(anyString());
        verify(idempotencyRedisRepository).isProcessing(idempotencyKey);
    }

    @Test
    void testPreHandle_NoCachedResult() throws Exception {
        // Given
        String idempotencyKey = "test-key";
        when(request.getHeader("Idempotency-Key")).thenReturn(idempotencyKey);
        when(idempotencyRedisRepository.hasSucceedResult(idempotencyKey)).thenReturn(false);
        when(idempotencyRedisRepository.isProcessing(idempotencyKey)).thenReturn(false);

        // When
        boolean result = interceptor.preHandle(request, response, new Object());

        // Then
        assertTrue(result);
        verify(idempotencyRedisRepository).saveStatusProcessing(idempotencyKey);
    }
}