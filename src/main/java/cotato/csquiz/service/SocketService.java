package cotato.csquiz.service;

import cotato.csquiz.config.jwt.JwtTokenProvider;
import cotato.csquiz.controller.dto.socket.EducationCloseRequest;
import cotato.csquiz.controller.dto.socket.QuizOpenRequest;
import cotato.csquiz.controller.dto.socket.QuizSocketRequest;
import cotato.csquiz.controller.dto.socket.SocketTokenDto;
import cotato.csquiz.domain.entity.Education;
import cotato.csquiz.domain.entity.KingMember;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.enums.EducationStatus;
import cotato.csquiz.domain.enums.QuizStatus;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.global.websocket.WebSocketHandler;
import cotato.csquiz.repository.EducationRepository;
import cotato.csquiz.repository.QuizRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final KingMemberService kingMemberService;

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
        if (quiz.getNumber() == 9) {
            List<KingMember> kingMembers = kingMemberService.calculateKingMember(quiz.getEducation());
            kingMemberService.saveKingMembers(kingMembers);
            kingMemberService.saveWinnerIfKingMemberIsOne(quiz.getEducation());
            webSocketHandler.stopQuiz(quiz);
        }
        if (quiz.getNumber() == 10) {
            kingMemberService.saveWinnerIfNoWinnerExist(quiz);
            webSocketHandler.stopQuiz(quiz);
        }
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
}
