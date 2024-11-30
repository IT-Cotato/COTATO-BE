package org.cotato.csquiz.api.member.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.member.dto.CreateGenerationMemberRequest;
import org.cotato.csquiz.api.member.dto.UpdateGenerationMemberRoleRequest;
import org.cotato.csquiz.domain.auth.service.GenerationMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "기수별 활동 멤버 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/api/generation-member")
public class GenerationMemberController {

    private final GenerationMemberService generationMemberService;

    @PostMapping
    public ResponseEntity<Void> addGenerationMember(@Valid @RequestBody CreateGenerationMemberRequest request) {
        generationMemberService.addGenerationMember(request.memberId(), request.generationId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping
    public ResponseEntity<Void> updateGenerationMemberRole(
            @Valid @RequestBody UpdateGenerationMemberRoleRequest request) {
        generationMemberService.updateGenerationMemberRole(request.generationMemberId(), request.role());
        return ResponseEntity.noContent().build();
    }
}
