package org.cotato.csquiz.common.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.cotato.csquiz.api.socket.dto.CsQuizStopResponse;
import org.cotato.csquiz.api.socket.dto.EducationResultResponse;
import org.cotato.csquiz.api.socket.dto.QuizStartResponse;
import org.cotato.csquiz.api.socket.dto.QuizStatusResponse;
import org.cotato.csquiz.api.socket.dto.QuizStopResponse;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.domain.auth.enums.MemberRoleGroup;
import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.enums.QuizStatus;
import org.cotato.csquiz.domain.education.repository.QuizRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
	private static final ConcurrentHashMap<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<>();
	private static final String KING_COMMAND = "king";
	private static final String WINNER_COMMAND = "winner";
	private static final String SHOW_COMMAND = "show";
	private static final String START_COMMAND = "start";
	private static final String EXIT_COMMAND = "exit";
	private static final String MEMBER_ID_KEY = "memberId";
	private static final String EDUCATION_ID_KEY = "educationId";
	private static final String ROLE_KEY = "role";

	private static final String KEY_DELIMITER = "@";
	private static final CloseStatus ATTEMPT_NEW_CONNECTION = new CloseStatus(4001, "new connection request");

	private final QuizRepository quizRepository;

	private final SocketSender socketSender;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws IOException {
		String memberId = findAttributeByToken(session, MEMBER_ID_KEY);
		Long educationId = Long.parseLong(findAttributeByToken(session, EDUCATION_ID_KEY));
		String role = findAttributeByToken(session, ROLE_KEY);
		MemberRole memberRole = MemberRole.fromKey(role);

		if (MemberRoleGroup.hasRole(MemberRoleGroup.CLIENTS, memberRole)) {
			handleSessionReplacement(memberId, CLIENTS);
			CLIENTS.put(memberId, session);
		}

		if (MemberRoleGroup.hasRole(MemberRoleGroup.CLIENTS, memberRole)) {
			sendCurrentOpenQuiz(educationId, session);
		}

		log.info("[세션 연결] {}, 연결된 세션: {}", memberId, session.getId());
	}

	private void handleSessionReplacement(String memberId, ConcurrentHashMap<String, WebSocketSession> managers)
		throws IOException {
		if (managers.containsKey(memberId)) {
			managers.get(memberId).close(ATTEMPT_NEW_CONNECTION);
			managers.remove(memberId);
		}
	}

	private void sendCurrentOpenQuiz(Long educationId, WebSocketSession session) {
		Optional<Quiz> maybeQuiz = quizRepository.findByStatusAndEducationId(QuizStatus.QUIZ_ON,
			educationId);

		QuizStatusResponse response = maybeQuiz.map(quiz -> QuizStatusResponse.builder()
				.command(SHOW_COMMAND)
				.quizId(quiz.getId())
				.status(quiz.getStatus())
				.start(quiz.getStart())
				.build())
			.orElse(QuizStatusResponse.builder()
				.command(SHOW_COMMAND)
				.build());
		socketSender.sendMessage(session, response);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		String memberId = findAttributeByToken(session, MEMBER_ID_KEY);
		String roleAttribute = findAttributeByToken(session, ROLE_KEY);
		MemberRole memberRole = MemberRole.fromKey(roleAttribute);

		if (MemberRoleGroup.hasRole(MemberRoleGroup.CLIENTS, memberRole)) {
			CLIENTS.remove(memberId);
		}
		log.info("[세션 종료] {}, 종료 코드: {}", memberId, status);
	}

	public void accessQuiz(Long quizId) {
		QuizStatusResponse response = QuizStatusResponse.builder()
			.quizId(quizId)
			.command(SHOW_COMMAND)
			.status(QuizStatus.QUIZ_ON)
			.start(QuizStatus.QUIZ_OFF)
			.build();

		log.info("[문제 {} 접근 허용]", quizId);
		log.info("[연결된 사용자 : {}]", CLIENTS.keySet());

		KeySetView<String, WebSocketSession> beforeUsers = CLIENTS.keySet();
		Collection<CompletableFuture<Void>> tasks = new ArrayList<>();
		for (WebSocketSession clientSession : CLIENTS.values()) {
			tasks.add(socketSender.sendMessage(clientSession, response));
		}

		CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
		logConnectionFailedUser(beforeUsers, CLIENTS.keySet());
	}

	public void startQuiz(Long quizId) {
		QuizStartResponse response = QuizStartResponse.builder()
			.quizId(quizId)
			.command(START_COMMAND)
			.build();

		log.info("[문제 {} 풀이 허용]", quizId);
		log.info("[연결된 사용자 : {}]", CLIENTS.keySet());

		KeySetView<String, WebSocketSession> beforeUsers = CLIENTS.keySet();
		Collection<CompletableFuture<Void>> tasks = new ArrayList<>();
		for (WebSocketSession clientSession : CLIENTS.values()) {
			tasks.add(socketSender.sendMessage(clientSession, response));
		}
		CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();

		logConnectionFailedUser(beforeUsers, CLIENTS.keySet());
	}

	private void logConnectionFailedUser(KeySetView<String, WebSocketSession> beforeUsers,
		KeySetView<String, WebSocketSession> strings) {
		Set<String> disconnectedUser = SetUtils.difference(beforeUsers, strings).toSet();
		if (!CollectionUtils.isEmpty(disconnectedUser)) {
			log.info("disconnected user exists");
			disconnectedUser.forEach(memberId -> {
				log.info("disconnected member id: <{}>", memberId);
			});
		}
	}

	public void stopQuiz(Long quizId) {
		QuizStopResponse response = QuizStopResponse.from(quizId);
		Collection<CompletableFuture<Void>> tasks = new ArrayList<>();
		for (WebSocketSession clientSession : CLIENTS.values()) {
			tasks.add(socketSender.sendMessage(clientSession, response));
		}
		CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
	}

	public void sendKingMemberCommand(Long educationId) {
		EducationResultResponse response = EducationResultResponse.of(KING_COMMAND, educationId);

		for (WebSocketSession clientSession : CLIENTS.values()) {
			socketSender.sendMessage(clientSession, response);
		}
	}

	public void sendWinnerCommand(Long educationId) {
		EducationResultResponse response = EducationResultResponse.of(WINNER_COMMAND, educationId);

		for (WebSocketSession clientSession : CLIENTS.values()) {
			socketSender.sendMessage(clientSession, response);
		}
	}

	public void stopEducation(Long educationId) {
		CsQuizStopResponse response = CsQuizStopResponse.from(EXIT_COMMAND, educationId);
		for (WebSocketSession clientSession : CLIENTS.values()) {
			socketSender.sendMessage(clientSession, response);
		}
	}

	private String findAttributeByToken(WebSocketSession session, String key) {
		return session.getAttributes().get(key).toString();
	}
}
