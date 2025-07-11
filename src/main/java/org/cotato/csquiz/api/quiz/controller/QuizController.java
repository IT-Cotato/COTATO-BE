package org.cotato.csquiz.api.quiz.controller;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import io.swagger.v3.oas.annotations.Operation;
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
import org.cotato.csquiz.common.role.RoleAuthority;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
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

    @RoleAuthority(MemberRole.MANAGER)
    @PostMapping(value = "/adds", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> addAllQuizzes(@ModelAttribute CreateQuizzesRequest request,
                                              @RequestParam("educationId") Long educationId) throws ImageException {
        quizService.createQuizzes(educationId, request.getMultiples(), request.getShortQuizzes());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "모든 퀴즈 문제 조회 API")
    @GetMapping("/all")
    public ResponseEntity<AllQuizzesResponse> findAllQuizzesForEducationTeam(@RequestParam("educationId") Long educationId) {
        return ResponseEntity.ok(quizService.findAllQuizzesForEducationTeam(educationId));
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<QuizResponse> findOneQuizForMember(@PathVariable("quizId") Long quizId) {
        return ResponseEntity.ok().body(quizService.getQuizById(quizId));
    }

    @RoleAuthority(MemberRole.MANAGER)
    @GetMapping("/cs-admin/all")
    public ResponseEntity<AllQuizzesInCsQuizResponse> findAllQuizzesForAdminCsQuiz(
            @RequestParam("educationId") Long educationId) {
        return ResponseEntity.ok(quizService.findAllQuizzesForAdminCsQuiz(educationId));
    }

    @RoleAuthority(MemberRole.MANAGER)
    @GetMapping("/cs-admin")
    public ResponseEntity<QuizInfoInCsQuizResponse> findQuizForAdminCsQuiz(@RequestParam("quizId") Long quizId) {
        return ResponseEntity.ok(quizService.findQuizForAdminCsQuiz(quizId));
    }

    @RoleAuthority(MemberRole.MANAGER)
    @PostMapping("/cs-admin/answer/add")
    public ResponseEntity<Void> addAdditionalAnswer(@RequestBody @Valid AddAdditionalAnswerRequest request) {
        quizService.addAdditionalAnswer(request);

        return ResponseEntity.noContent().build();
    }

    @RoleAuthority(MemberRole.MANAGER)
    @GetMapping("/cs-admin/results")
    public ResponseEntity<List<QuizResultInfo>> quizResults(@RequestParam("educationId") Long educationId) {
        return ResponseEntity.ok(quizService.createQuizResults(educationId));
    }
}
