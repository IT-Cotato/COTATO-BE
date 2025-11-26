package org.cotato.csquiz.common.websocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SocketSender {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Async("quizSendThreadPoolExecutor")
	public CompletableFuture<Void> sendMessage(WebSocketSession session, Object sendValue) {
		try {
			String json = objectMapper.writeValueAsString(sendValue);
			TextMessage responseMessage = new TextMessage(json);
			session.sendMessage(responseMessage);
			return CompletableFuture.completedFuture(null);
		} catch (IOException e) {
			throw new AppException(ErrorCode.WEBSOCKET_SEND_EXCEPTION);
		}
	}
}
