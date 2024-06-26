package org.cotato.csquiz.api.session.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.session.dto.AddSessionRequest;
import org.cotato.csquiz.api.session.dto.AddSessionResponse;
import org.cotato.csquiz.api.session.dto.CsEducationOnSessionNumberResponse;
import org.cotato.csquiz.api.session.dto.SessionListResponse;
import org.cotato.csquiz.api.session.dto.UpdateSessionDescriptionRequest;
import org.cotato.csquiz.api.session.dto.UpdateSessionNumberRequest;
import org.cotato.csquiz.api.session.dto.UpdateSessionPhotoRequest;
import org.cotato.csquiz.api.session.dto.UpdateSessionRequest;
import org.cotato.csquiz.domain.generation.service.SessionService;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/session")
@RequiredArgsConstructor
@Slf4j
public class SessionController {

    private final SessionService sessionService;

    @GetMapping("")
    public ResponseEntity<List<SessionListResponse>> findSessionsByGenerationId(@RequestParam Long generationId) {
        return ResponseEntity.status(HttpStatus.OK).body(sessionService.findSessionsByGenerationId(generationId));
    }

    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<AddSessionResponse> addSession(@ModelAttribute @Valid AddSessionRequest request)
            throws ImageException {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.addSession(request));
    }

    @PatchMapping(value = "/update", consumes = "multipart/form-data")
    public ResponseEntity<Void> updateSession(@ModelAttribute @Valid UpdateSessionRequest request)
            throws ImageException {
        sessionService.updateSession(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/number")
    public ResponseEntity<Void> updateSessionNumber(@RequestBody @Valid UpdateSessionNumberRequest request) {
        sessionService.updateSessionNumber(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/description")
    public ResponseEntity<Void> updateSessionDescription(@RequestBody @Valid UpdateSessionDescriptionRequest request) {
        sessionService.updateSessionDescription(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/update/photo", consumes = "multipart/form-data")
    public ResponseEntity<Void> updateSessionPhoto(@ModelAttribute @Valid UpdateSessionPhotoRequest request)
            throws ImageException {
        sessionService.updateSessionPhoto(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cs-on")
    public ResponseEntity<List<CsEducationOnSessionNumberResponse>> findAllCsOnSessionsByGenerationId(
            @RequestParam Long generationId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(sessionService.findAllNotLinkedCsOnSessionsByGenerationId(generationId));
    }
}
