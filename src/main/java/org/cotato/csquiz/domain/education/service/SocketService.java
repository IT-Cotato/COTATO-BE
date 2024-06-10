package org.cotato.csquiz.domain.education.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.socket.dto.EducationCloseRequest;
import org.cotato.csquiz.api.socket.dto.QuizOpenRequest;
import org.cotato.csquiz.api.socket.dto.QuizSocketRequest;
import org.cotato.csquiz.api.socket.dto.SocketTokenDto;
import org.cotato.csquiz.common.config.jwt.JwtTokenProvider;
import org.cotato.csquiz.domain.education.entity.Education;
import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.enums.QuizStatus;
import org.cotato.csquiz.domain.education.repository.EducationRepository;
import org.cotato.csquiz.domain.education.repository.QuizRepository;
import org.cotato.csquiz.domain.education.enums.EducationStatus;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.websocket.WebSocketHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SocketService {

    private final WebSocketHandler webSocketHandler;
    private final QuizRepository quizRepository;
    private final EducationRepository educationRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void openCSQuiz(QuizOpenRequest request) {
        Education education = findEducationById(request.educationId());

        checkEducationBefore(education);

        education.updateStatus(EducationStatus.ONGOING);
    }

    private void checkEducationBefore(Education education) {
        if (EducationStatus.BEFORE != education.getStatus()) {
            throw new AppException(ErrorCode.EDUCATION_STATUS_NOT_BEFORE);
        }
    }

    @Transactional
    public void accessQuiz(QuizSocketRequest request) {
        Quiz quiz = findQuizById(request.quizId());

        checkEducationOpen(quiz.getEducation());

        makeAllStartFalse();
        makeAllStatusFalse();

        quiz.updateStatus(QuizStatus.QUIZ_ON);
        webSocketHandler.accessQuiz(quiz.getId());
    }

    @Transactional
    public void startQuizSolve(QuizSocketRequest request) {
        Quiz quiz = findQuizById(request.quizId());

        checkEducationOpen(quiz.getEducation());
        checkQuizIsStarted(quiz);

        sleepRandomTime(quiz);
        quiz.updateStart(QuizStatus.QUIZ_ON);

        webSocketHandler.startQuiz(quiz.getId());
    }

    private void checkQuizIsStarted(Quiz quiz) {
        if (quiz.getStatus().equals(QuizStatus.QUIZ_OFF)) {
            throw new AppException(ErrorCode.QUIZ_ACCESS_DENIED);
        }
    }

    private void sleepRandomTime(Quiz quiz) {
        try {
            Thread.sleep(1000L * quiz.getAppearSecond());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Transactional
    public void denyQuiz(QuizSocketRequest request) {
        Quiz quiz = findQuizById(request.quizId());

        checkEducationOpen(quiz.getEducation());

        quiz.updateStatus(QuizStatus.QUIZ_OFF);
        quiz.updateStart(QuizStatus.QUIZ_OFF);
    }

    @Transactional
    public void stopQuizSolve(QuizSocketRequest request) {
        Quiz quiz = findQuizById(request.quizId());
        checkEducationOpen(quiz.getEducation());

        quiz.updateStart(QuizStatus.QUIZ_OFF);

        webSocketHandler.stopQuiz(quiz);
    }

    @Transactional
    public void stopAllQuiz(EducationCloseRequest request) {
        closeAllFlags();

        Education education = findEducationById(request.educationId());

        education.updateStatus(EducationStatus.FINISHED);
        webSocketHandler.stopAllQuiz(education.getId());
    }

    @Transactional
    public void closeAllFlags() {
        makeAllStatusFalse();
        makeAllStartFalse();
    }

    @Transactional
    public SocketTokenDto createSocketToken(String authorizationHeader) {
        String token = jwtTokenProvider.resolveAccessToken(authorizationHeader);
        String role = jwtTokenProvider.getRole(token);
        Long memberId = jwtTokenProvider.getMemberId(token);
        jwtTokenProvider.checkMemberExist(memberId);

        String socketToken = jwtTokenProvider.createSocketToken(memberId, role);
        log.info("[ 소켓 전용 토큰 발급 완료 ]");
        return SocketTokenDto.from(socketToken);
    }

    private void makeAllStatusFalse() {
        quizRepository.findAllByStatus(QuizStatus.QUIZ_ON)
                .forEach(quiz -> quiz.updateStatus(QuizStatus.QUIZ_OFF));
    }

    private void makeAllStartFalse() {
        quizRepository.findAllByStart(QuizStatus.QUIZ_ON)
                .forEach(quiz -> quiz.updateStart(QuizStatus.QUIZ_OFF));
    }

    private void checkEducationOpen(Education education) {
        if (EducationStatus.ONGOING != education.getStatus()) {
            throw new AppException(ErrorCode.EDUCATION_CLOSED);
        }
    }

    private Quiz findQuizById(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("해당 퀴즈를 찾을 수 없습니다."));
    }

    private Education findEducationById(Long educationId) {
        return educationRepository.findById(educationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 교육을 찾을 수 없습니다."));
    }

    public void sendKingCommand(Long educationId) {
        webSocketHandler.sendKingMemberCommand(educationId);
    }

    public void sendWinnerCommand(Long educationId) {
        webSocketHandler.sendWinnerCommand(educationId);
    }
}
