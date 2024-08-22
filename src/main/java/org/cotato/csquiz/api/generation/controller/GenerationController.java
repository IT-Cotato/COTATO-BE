package org.cotato.csquiz.api.generation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.generation.dto.AddGenerationRequest;
import org.cotato.csquiz.api.generation.dto.AddGenerationResponse;
import org.cotato.csquiz.api.generation.dto.ChangeGenerationPeriodRequest;
import org.cotato.csquiz.api.generation.dto.ChangeRecruitingStatusRequest;
import org.cotato.csquiz.api.generation.dto.GenerationInfoResponse;
import org.cotato.csquiz.domain.generation.service.GenerationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "기수 관련 API")
@RestController
@RequestMapping("/v1/api/generation")
@RequiredArgsConstructor
@Slf4j
public class GenerationController {

    private final GenerationService generationService;

    @GetMapping
    public ResponseEntity<List<GenerationInfoResponse>> findGenerations() {
        return ResponseEntity.ok().body(generationService.findGenerations());
    }

    @PostMapping("/add")
    public ResponseEntity<AddGenerationResponse> addGeneration(@RequestBody @Valid AddGenerationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(generationService.addGeneration(request));
    }

    @PatchMapping("/recruiting")
    public ResponseEntity<Void> changeRecruitingStatus(@RequestBody @Valid ChangeRecruitingStatusRequest request) {
        generationService.changeRecruitingStatus(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/period")
    public ResponseEntity<Void> changeGenerationPeriod(@RequestBody @Valid ChangeGenerationPeriodRequest request) {
        generationService.changeGenerationPeriod(request);
        return ResponseEntity.noContent().build();
    }
}
