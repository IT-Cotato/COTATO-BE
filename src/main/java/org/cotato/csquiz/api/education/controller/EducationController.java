package org.cotato.csquiz.api.education.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.education.dto.AllEducationResponse;
import org.cotato.csquiz.api.education.dto.CreateEducationRequest;
import org.cotato.csquiz.api.education.dto.CreateEducationResponse;
import org.cotato.csquiz.api.education.dto.EducationIdOfQuizResponse;
import org.cotato.csquiz.api.education.dto.FindEducationStatusResponse;
import org.cotato.csquiz.api.education.dto.UpdateEducationRequest;
import org.cotato.csquiz.api.education.dto.WinnerInfoResponse;
import org.cotato.csquiz.api.quiz.dto.KingMemberInfo;
import org.cotato.csquiz.domain.education.service.EducationService;
import org.cotato.csquiz.domain.education.service.KingMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/education")
@RequiredArgsConstructor
@Slf4j
public class EducationController {

    private final EducationService educationService;
    private final KingMemberService kingMemberService;

    @GetMapping
    public ResponseEntity<List<AllEducationResponse>> findEducationListByGeneration(
            @RequestParam(value = "generationId") Long generationId) {
        return ResponseEntity.ok().body(educationService.findEducationListByGeneration(generationId));
    }

    @GetMapping("/status")
    public ResponseEntity<FindEducationStatusResponse> findEducationStatus(
            @RequestParam(value = "educationId") Long educationId) {
        return ResponseEntity.ok().body(educationService.findEducationStatus(educationId));
    }

    @PostMapping("/add")
    public ResponseEntity<CreateEducationResponse> createEducation(@RequestBody @Valid CreateEducationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(educationService.createEducation(request));
    }

    @PatchMapping("/update")
    public ResponseEntity<Void> updateEducation(@RequestBody @Valid UpdateEducationRequest request) {
        educationService.updateSubjectAndNumber(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/from")
    public ResponseEntity<EducationIdOfQuizResponse> findEducationId(@RequestParam("quizId") Long quizId) {
        log.info("[{} quizId의 educationId 조회 컨트롤러]", quizId);
        return ResponseEntity.ok().body(educationService.findEducationIdOfQuizId(quizId));
    }

    @GetMapping("/result/kings")
    public ResponseEntity<List<KingMemberInfo>> findFinalKingMembers(@RequestParam("educationId") Long educationId) {
        log.info("[{} 교육 결승진출자 조회 컨트롤러]", educationId);
        return ResponseEntity.ok().body(educationService.findKingMemberInfo(educationId));
    }

    @PostMapping("/result/kings")
    public ResponseEntity<Void> calculateKingMembers(@RequestParam("educationId") Long educationId) {
        log.info("[{} 교육 결승진출자 계산하기]", educationId);
        kingMemberService.saveKingMember(educationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/send/kings")
    public ResponseEntity<Void> sendKingCommand(@RequestParam("educationId") Long educationId) {
        log.info("[{} 교육 결승진출자 재전송하기]", educationId);
        kingMemberService.sendKingCommand(educationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/result/winner")
    public ResponseEntity<WinnerInfoResponse> findWinner(@RequestParam("educationId") Long educationId) {
        log.info("[{} 교육 우승자 조회 컨트롤러]", educationId);
        return ResponseEntity.ok().body(educationService.findWinner(educationId));
    }

    @PostMapping("/result/winner")
    public ResponseEntity<Void> calculateWinner(@RequestParam("educationId") Long educationId) {
        log.info("[{} 교육 우승자 계산]", educationId);
        kingMemberService.calculateWinner(educationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/send/winner")
    public ResponseEntity<Void> sendWinnerCommand(@RequestParam("educationId") Long educationId) {
        log.info("[{} 교육 결승진출자 재전송하기]", educationId);
        kingMemberService.sendWinnerCommand(educationId);
        return ResponseEntity.noContent().build();
    }
}
