package org.cotato.csquiz.api.quiz.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.quiz.dto.AddAdditionalAnswerRequest;
import org.cotato.csquiz.api.quiz.dto.AllQuizzesInCsQuizResponse;
import org.cotato.csquiz.api.quiz.dto.AllQuizzesResponse;
import org.cotato.csquiz.api.quiz.dto.CreateQuizzesRequest;
import org.cotato.csquiz.api.quiz.dto.QuizInfoInCsQuizResponse;
import org.cotato.csquiz.api.quiz.dto.QuizResponse;
import org.cotato.csquiz.api.quiz.dto.QuizResultInfo;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.cotato.csquiz.domain.education.service.QuizService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping(value = "/adds", consumes = "multipart/form-data")
    public ResponseEntity<Void> addAllQuizzes(@ModelAttribute CreateQuizzesRequest request,
                                              @RequestParam("educationId") Long educationId) throws ImageException {
        quizService.createQuizzes(educationId, request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/all")
    public ResponseEntity<AllQuizzesResponse> findAllQuizzesForEducationTeam(
            @RequestParam("educationId") Long educationId) {
        return ResponseEntity.ok(quizService.findAllQuizzesForEducationTeam(educationId));
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<QuizResponse> findOneQuizForMember(@PathVariable("quizId") Long quizId) {
        return ResponseEntity.ok().body(quizService.findOneQuizForMember(quizId));
    }

    @GetMapping("/cs-admin/all")
    public ResponseEntity<AllQuizzesInCsQuizResponse> findAllQuizzesForAdminCsQuiz(
            @RequestParam("educationId") Long educationId) {
        return ResponseEntity.ok(quizService.findAllQuizzesForAdminCsQuiz(educationId));
    }

    @GetMapping("/cs-admin")
    public ResponseEntity<QuizInfoInCsQuizResponse> findQuizForAdminCsQuiz(@RequestParam("quizId") Long quizId) {
        return ResponseEntity.ok(quizService.findQuizForAdminCsQuiz(quizId));
    }

    @PostMapping("/cs-admin/answer/add")
    public ResponseEntity<Void> addAdditionalAnswer(@RequestBody @Valid AddAdditionalAnswerRequest request) {
        quizService.addAdditionalAnswer(request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cs-admin/results")
    public ResponseEntity<List<QuizResultInfo>> quizResults(@RequestParam("educationId") Long educationId) {
        return ResponseEntity.ok(quizService.createQuizResults(educationId));
    }
}
