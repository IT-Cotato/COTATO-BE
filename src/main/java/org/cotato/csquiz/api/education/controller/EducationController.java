package org.cotato.csquiz.api.education.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.education.dto.AllEducationResponse;
import org.cotato.csquiz.api.education.dto.CreateEducationRequest;
import org.cotato.csquiz.api.education.dto.CreateEducationResponse;
import org.cotato.csquiz.api.education.dto.EducationCountResponse;
import org.cotato.csquiz.api.education.dto.EducationIdOfQuizResponse;
import org.cotato.csquiz.api.education.dto.FindEducationStatusResponse;
import org.cotato.csquiz.api.education.dto.UpdateEducationRequest;
import org.cotato.csquiz.api.education.dto.WinnerInfoResponse;
import org.cotato.csquiz.api.quiz.dto.KingMemberInfo;
import org.cotato.csquiz.common.role.RoleAuthority;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
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

    @Operation(summary = "교육 추가 API")
    @RoleAuthority(MemberRole.MANAGER)
    @PostMapping
    public ResponseEntity<CreateEducationResponse> createEducation(@RequestBody @Valid CreateEducationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(educationService.createEducation(request.subject(), request.generationId(), request.educationNumber()));
    }

    @Operation(summary = "교육 수정 API")
    @RoleAuthority(MemberRole.MANAGER)
    @PatchMapping
    public ResponseEntity<Void> updateEducation(@RequestBody @Valid UpdateEducationRequest request) {
        educationService.updateSubjectAndNumber(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/from")
    public ResponseEntity<EducationIdOfQuizResponse> findEducationId(@RequestParam("quizId") Long quizId) {
        log.info("[{} quizId의 educationId 조회 컨트롤러]", quizId);
        return ResponseEntity.ok().body(educationService.findEducationIdOfQuizId(quizId));
    }

    @GetMapping("/kings")
    public ResponseEntity<List<KingMemberInfo>> findFinalKingMembers(@RequestParam("educationId") Long educationId) {
        log.info("[{} 교육 결승진출자 조회 컨트롤러]", educationId);
        return ResponseEntity.ok().body(kingMemberService.findKingMemberInfo(educationId));
    }

    @RoleAuthority(MemberRole.MANAGER)
    @PostMapping("/kings")
    public ResponseEntity<Void> calculateKingMembers(@RequestParam("educationId") Long educationId) {
        kingMemberService.saveKingMember(educationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/winner")
    public ResponseEntity<WinnerInfoResponse> findWinner(@RequestParam("educationId") Long educationId) {
        return ResponseEntity.ok().body(kingMemberService.findWinner(educationId));
    }

    @RoleAuthority(MemberRole.MANAGER)
    @PostMapping("/winner")
    public ResponseEntity<Void> calculateWinner(@RequestParam("educationId") Long educationId) {
        kingMemberService.calculateWinner(educationId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "교육 및 퀴즈 수 조회 API")
    @GetMapping("/counts")
    public ResponseEntity<EducationCountResponse> getEducationCounts() {
        return ResponseEntity.ok().body(educationService.getEducationCounts());
    }
}
