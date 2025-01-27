package org.cotato.csquiz.api.generation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.generation.dto.AddGenerationRequest;
import org.cotato.csquiz.api.generation.dto.AddGenerationResponse;
import org.cotato.csquiz.api.generation.dto.ChangeGenerationPeriodRequest;
import org.cotato.csquiz.api.generation.dto.ChangeRecruitingStatusRequest;
import org.cotato.csquiz.api.generation.dto.GenerationInfoResponse;
import org.cotato.csquiz.common.role.RoleAuthority;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.domain.generation.service.GenerationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "기수 관련 API")
@RestController
@RequestMapping("/v1/api/generations")
@RequiredArgsConstructor
public class GenerationController {

    private final GenerationService generationService;

    @GetMapping
    public ResponseEntity<List<GenerationInfoResponse>> findGenerations() {
        return ResponseEntity.ok().body(generationService.findGenerations());
    }

    @RoleAuthority(MemberRole.ADMIN)
    @PostMapping
    public ResponseEntity<AddGenerationResponse> addGeneration(@RequestBody @Valid AddGenerationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(generationService.addGeneration(request));
    }

    @RoleAuthority(MemberRole.ADMIN)
    @PatchMapping("/recruiting")
    public ResponseEntity<Void> changeRecruitingStatus(@RequestBody @Valid ChangeRecruitingStatusRequest request) {
        generationService.changeRecruitingStatus(request.generationId(), request.statement());
        return ResponseEntity.noContent().build();
    }

    @RoleAuthority(MemberRole.ADMIN)
    @PatchMapping("/{generationId}/period")
    public ResponseEntity<Void> changeGenerationPeriod(@RequestBody @Valid ChangeGenerationPeriodRequest request) {
        generationService.changeGenerationPeriod(request.generationId(), request.startDate(), request.endDate());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "현재 날짜 기준 세션 정보 반환 API")
    @GetMapping("/current")
    public ResponseEntity<GenerationInfoResponse> findCurrentGeneration() {
        LocalDate currentDate = LocalDate.now();
        return ResponseEntity.ok().body(generationService.findCurrentGeneration(currentDate));
    }

    @Operation(summary = "기수 단건 조회 API")
    @GetMapping("/{generationId}")
    public ResponseEntity<GenerationInfoResponse> findGenerationById(
            @PathVariable("generationId") Long generationId) {
        return ResponseEntity.ok().body(generationService.findGenerationById(generationId));
    }
}
