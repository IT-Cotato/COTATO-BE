package org.cotato.csquiz.common.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cotato.csquiz.api.socket.dto.CsQuizStopResponse;
import org.cotato.csquiz.api.socket.dto.EducationResultResponse;
import org.cotato.csquiz.api.socket.dto.QuizStartResponse;
import org.cotato.csquiz.api.socket.dto.QuizStatusResponse;
import org.cotato.csquiz.api.socket.dto.QuizStopResponse;
import org.cotato.csquiz.domain.education.entity.Education;
import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.domain.auth.enums.MemberRoleGroup;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.education.repository.QuizRepository;
import org.cotato.csquiz.domain.education.enums.QuizStatus;
import org.cotato.csquiz.common.error.ErrorCode;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.domain.education.service.EducationService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
    private static final ConcurrentHashMap<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, WebSocketSession> MANAGERS = new ConcurrentHashMap<>();
    private static final String KING_COMMAND = "king";
    private static final String WINNER_COMMAND = "winner";
    private static final String SHOW_COMMAND = "show";
    private static final String START_COMMAND = "start";
    private static final String EXIT_COMMAND = "exit";
    private static final String MEMBER_ID_KEY = "memberId";
    private static final String EDUCATION_ID_KEY = "educationId";
    private static final String ROLE_KEY = "role";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final QuizRepository quizRepository;
    private final EducationService educationService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String memberId = findAttributeByToken(session, MEMBER_ID_KEY);
        Long educationId = Long.parseLong(findAttributeByToken(session, EDUCATION_ID_KEY));
        String role = findAttributeByToken(session, ROLE_KEY);
        MemberRole memberRole = MemberRole.fromKey(role);

        addMemberToSession(memberId, session);

        if (MemberRoleGroup.hasRole(MemberRoleGroup.CLIENTS, memberRole)) {
            sendQuiz(educationId, session);
        }

        log.info("[세션 연결] {}", memberId);
    }

    private void sendQuiz(Long educationId, WebSocketSession session) {
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
        sendMessage(session, response);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String memberId = findAttributeByToken(session, MEMBER_ID_KEY);
        String roleAttribute = findAttributeByToken(session, ROLE_KEY);
        MemberRole memberRole = MemberRole.fromKey(roleAttribute);

        if (MemberRoleGroup.hasRole(MemberRoleGroup.CLIENTS, memberRole)) {
            CLIENTS.remove(memberId);
        } else {
            MANAGERS.remove(memberId);
        }

        log.info("[세션 종료] {}", memberId);
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
        for (WebSocketSession clientSession : CLIENTS.values()) {
            sendMessage(clientSession, response);
        }
        log.info("[문제 전송 후 사용자 : {}]", CLIENTS.keySet());
    }

    public void startQuiz(Long quizId) {
        QuizStartResponse response = QuizStartResponse.builder()
                .quizId(quizId)
                .command(START_COMMAND)
                .build();

        log.info("[문제 {} 풀이 허용]", quizId);
        log.info("[연결된 사용자 : {}]", CLIENTS.keySet());
        for (WebSocketSession clientSession : CLIENTS.values()) {
            sendMessage(clientSession, response);
        }
        log.info("[전구 전송 후 사용자 : {}]", CLIENTS.keySet());
    }

    public void stopQuiz(Quiz quiz) {
        QuizStopResponse response = QuizStopResponse.from(quiz.getId());
        for (WebSocketSession clientSession : CLIENTS.values()) {
            sendMessage(clientSession, response);
        }
    }

    public void sendKingMemberCommand(Long educationId) {
        EducationResultResponse response = EducationResultResponse.of(KING_COMMAND, educationId);

        for (WebSocketSession clientSession : CLIENTS.values()) {
            sendMessage(clientSession, response);
        }
    }

    public void sendWinnerCommand(Long educationId) {
        EducationResultResponse response = EducationResultResponse.of(WINNER_COMMAND, educationId);

        for (WebSocketSession clientSession : CLIENTS.values()) {
            sendMessage(clientSession, response);
        }
    }

    public void stopAllQuiz(Long educationId) {
        CsQuizStopResponse response = CsQuizStopResponse.from(EXIT_COMMAND, educationId);
        for (WebSocketSession clientSession : CLIENTS.values()) {
            sendMessage(clientSession, response);
        }
    }

    private void addMemberToSession(String memberId, WebSocketSession session) {
        String roleAttribute = findAttributeByToken(session, ROLE_KEY);
        MemberRole role = MemberRole.fromKey(roleAttribute);

        if (MemberRoleGroup.hasRole(MemberRoleGroup.CLIENTS, role)) {
            CLIENTS.put(memberId, session);
            log.info("{} connect with Session {} in CLIENTS", memberId, session);
        } else {
            MANAGERS.put(memberId, session);
            log.info("{} connect with Session {} in MANAGER", memberId, session);
        }
    }

    private String findAttributeByToken(WebSocketSession session, String key) {
        return session.getAttributes().get(key).toString();
    }

    private void sendMessage(WebSocketSession session, Object sendValue) {
        try {
            String json = objectMapper.writeValueAsString(sendValue);
            TextMessage responseMessage = new TextMessage(json);
            session.sendMessage(responseMessage);
        } catch (IOException e) {
            throw new AppException(ErrorCode.WEBSOCKET_SEND_EXCEPTION);
        }
    }
}
