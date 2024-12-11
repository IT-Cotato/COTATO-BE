package org.cotato.csquiz.api.quiz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.quiz.dto.RandomTutorialQuizResponse;
import org.cotato.csquiz.domain.education.service.RandomQuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CS 퀴즈 탭 정보", description = "외부인용 CS 퀴즈 탭 관련 API 입니다.")
@RestController
@RequestMapping("/v3/api/random-quiz")
@RequiredArgsConstructor
public class RandomQuizController {

    private final RandomQuizService randomQuizService;

    @Operation(summary = "외부인용 랜덤 퀴즈 반환 API")
    @GetMapping
    public ResponseEntity<RandomTutorialQuizResponse> pickRandomQuiz() {
        return ResponseEntity.ok().body(randomQuizService.pickRandomQuiz());
    }
}
