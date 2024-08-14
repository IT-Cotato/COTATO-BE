package org.cotato.csquiz.domain.education.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.socket.dto.QuizSocketRequest;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.education.entity.Education;
import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.enums.EducationStatus;
import org.cotato.csquiz.domain.education.enums.QuizStatus;
import org.cotato.csquiz.domain.education.repository.QuizRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizSolveService {

    private final SocketService socketService;
    private final QuizRepository quizRepository;
    private final EducationService educationService;

    @Transactional
    public void accessQuiz(QuizSocketRequest request) {
        Quiz quiz = findQuizById(request.quizId());

        checkEducationOpen(quiz.getEducation());

        educationService.closeAllFlags();

        quiz.updateStatus(QuizStatus.QUIZ_ON);
        socketService.accessQuiz(quiz.getId());
    }

    @Transactional
    public void denyQuiz(QuizSocketRequest request) {
        Quiz quiz = findQuizById(request.quizId());

        checkEducationOpen(quiz.getEducation());

        quiz.updateStatus(QuizStatus.QUIZ_OFF);
        quiz.updateStart(QuizStatus.QUIZ_OFF);
    }

    @Transactional
    public void startQuizSolve(QuizSocketRequest request) {
        Quiz quiz = findQuizById(request.quizId());

        checkEducationOpen(quiz.getEducation());
        checkQuizIsStarted(quiz);

        sleepRandomTime(quiz);
        quiz.updateStart(QuizStatus.QUIZ_ON);

        socketService.startQuizSolve(quiz.getId());
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
    public void stopQuizSolve(QuizSocketRequest request) {
        Quiz quiz = findQuizById(request.quizId());
        checkEducationOpen(quiz.getEducation());

        quiz.updateStart(QuizStatus.QUIZ_OFF);
        socketService.stopQuizSolve(quiz.getId());
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
}
