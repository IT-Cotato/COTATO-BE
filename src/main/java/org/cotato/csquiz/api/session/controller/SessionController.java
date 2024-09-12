package org.cotato.csquiz.api.session.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.session.dto.AddSessionImageRequest;
import org.cotato.csquiz.api.session.dto.AddSessionImageResponse;
import org.cotato.csquiz.api.session.dto.AddSessionRequest;
import org.cotato.csquiz.api.session.dto.AddSessionResponse;
import org.cotato.csquiz.api.session.dto.CsEducationOnSessionNumberResponse;
import org.cotato.csquiz.api.session.dto.DeleteSessionImageRequest;
import org.cotato.csquiz.api.session.dto.SessionListResponse;
import org.cotato.csquiz.api.session.dto.UpdateSessionNumberRequest;
import org.cotato.csquiz.api.session.dto.UpdateSessionImageOrderRequest;
import org.cotato.csquiz.api.session.dto.UpdateSessionRequest;
import org.cotato.csquiz.domain.generation.service.SessionImageService;
import org.cotato.csquiz.domain.generation.service.SessionService;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "세션 정보", description = "세션 관련 API 입니다.")
@RequestMapping("/v1/api/session")
@RequiredArgsConstructor
@Slf4j
public class SessionController {

    private final SessionService sessionService;
    private final SessionImageService sessionImageService;

    @Operation(summary = "세션 목록 반환 API")
    @GetMapping
    public ResponseEntity<List<SessionListResponse>> findSessionsByGenerationId(@RequestParam Long generationId) {
        return ResponseEntity.status(HttpStatus.OK).body(sessionService.findSessionsByGenerationId(generationId));
    }

    @Operation(summary = "CS ON인 세션 목록 반환 API")
    @GetMapping("/cs-on")
    public ResponseEntity<List<CsEducationOnSessionNumberResponse>> findAllCsOnSessionsByGenerationId(
            @RequestParam Long generationId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(sessionService.findAllNotLinkedCsOnSessionsByGenerationId(generationId));
    }

    @Operation(summary = "Session 추가 API")
    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<AddSessionResponse> addSession(@ModelAttribute @Valid AddSessionRequest request)
            throws ImageException {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.addSession(request));
    }

    @Operation(summary = "세션 수정 API")
    @PatchMapping(value = "/update")
    public ResponseEntity<Void> updateSession(@RequestBody @Valid UpdateSessionRequest request) {
        sessionService.updateSession(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "세션 숫자 변경 API")
    @PatchMapping("/number")
    public ResponseEntity<Void> updateSessionNumber(@RequestBody @Valid UpdateSessionNumberRequest request) {
        sessionService.updateSessionNumber(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "세션 사진 순서 변경 API")
    @PatchMapping("/image/order")
    public ResponseEntity<Void> updateSessionImageOrder(@RequestBody UpdateSessionImageOrderRequest request) {
        sessionImageService.updateSessionImageOrder(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "세션 사진 추가 API")
    @PostMapping(value = "/image", consumes = "multipart/form-data")
    public ResponseEntity<AddSessionImageResponse> additionalSessionImage(@ModelAttribute @Valid AddSessionImageRequest request)
            throws ImageException {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionImageService.additionalSessionImage(request));
    }

    @Operation(summary = "세션 사진 삭제 API")
    @DeleteMapping(value = "/image")
    public ResponseEntity<Void> deleteSessionImage(@RequestBody DeleteSessionImageRequest request) {
        sessionImageService.deleteSessionImage(request);
        return ResponseEntity.noContent().build();
    }
}
