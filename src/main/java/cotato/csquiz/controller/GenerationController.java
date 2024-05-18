package cotato.csquiz.controller;

import cotato.csquiz.controller.dto.generation.AddGenerationRequest;
import cotato.csquiz.controller.dto.generation.AddGenerationResponse;
import cotato.csquiz.controller.dto.generation.ChangeGenerationPeriodRequest;
import cotato.csquiz.controller.dto.generation.ChangeRecruitingStatusRequest;
import cotato.csquiz.controller.dto.generation.GenerationInfoResponse;
import cotato.csquiz.service.GenerationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
