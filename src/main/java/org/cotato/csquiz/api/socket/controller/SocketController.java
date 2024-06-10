package org.cotato.csquiz.api.socket.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.socket.dto.EducationCloseRequest;
import org.cotato.csquiz.api.socket.dto.QuizOpenRequest;
import org.cotato.csquiz.api.socket.dto.QuizSocketRequest;
import org.cotato.csquiz.api.socket.dto.SocketTokenDto;
import org.cotato.csquiz.domain.education.service.RecordService;
import org.cotato.csquiz.domain.education.service.SocketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/socket")
@RequiredArgsConstructor
@Slf4j
public class SocketController {

    private final SocketService socketService;
    private final RecordService recordService;

    @PatchMapping("/start/csquiz")
    public ResponseEntity<Void> openCSQuiz(@RequestBody @Valid QuizOpenRequest request) {
        socketService.openCSQuiz(request);
        recordService.saveAnswersToCache(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/access")
    public ResponseEntity<Void> accessQuiz(@RequestBody @Valid QuizSocketRequest request) {
        socketService.accessQuiz(request);
        recordService.saveAnswer(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/start")
    public ResponseEntity<Void> startQuizSolve(@RequestBody @Valid QuizSocketRequest request) {
        socketService.startQuizSolve(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/deny")
    public ResponseEntity<Void> denyQuiz(@RequestBody @Valid QuizSocketRequest request) {
        socketService.denyQuiz(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/stop")
    public ResponseEntity<Void> stopQuizSolve(@RequestBody @Valid QuizSocketRequest request) {
        socketService.stopQuizSolve(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/close/csquiz")
    public ResponseEntity<Void> stopAllQuiz(@RequestBody @Valid EducationCloseRequest request) {
        socketService.stopAllQuiz(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/token")
    public ResponseEntity<SocketTokenDto> makeSocketToken(@RequestHeader("Authorization") String authorizationHeader) {
        return ResponseEntity.ok(socketService.createSocketToken(authorizationHeader));
    }


    @PostMapping("/kings")
    public ResponseEntity<Void> sendKingCommand(@RequestParam("educationId") Long educationId) {
        log.info("[{} 교육 결승진출자 재전송하기]", educationId);
        socketService.sendKingCommand(educationId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/winner")
    public ResponseEntity<Void> sendWinnerCommand(@RequestParam("educationId") Long educationId) {
        log.info("[{} 교육 결승진출자 재전송하기]", educationId);
        socketService.sendWinnerCommand(educationId);
        return ResponseEntity.noContent().build();
    }
}
