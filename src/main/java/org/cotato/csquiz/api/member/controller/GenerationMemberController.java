package org.cotato.csquiz.api.member.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.member.dto.CreateGenerationMemberRequest;
import org.cotato.csquiz.api.member.dto.DeleteGenerationMemberRequest;
import org.cotato.csquiz.api.member.dto.GenerationMemberInfoResponse;
import org.cotato.csquiz.api.member.dto.UpdateGenerationMemberRoleRequest;
import org.cotato.csquiz.domain.auth.service.GenerationMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "기수별 활동 멤버 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/api/generation-member")
public class GenerationMemberController {

    private final GenerationMemberService generationMemberService;

    @GetMapping
    public ResponseEntity<GenerationMemberInfoResponse> findGenerationMember(
            @RequestParam("generationId") Long generationId) {
        return ResponseEntity.ok().body(generationMemberService.findGenerationMemberByGeneration(generationId));
    }

    @PostMapping
    public ResponseEntity<Void> addGenerationMember(@RequestBody @Valid CreateGenerationMemberRequest request) {
        generationMemberService.addGenerationMember(request.generationId(), request.memberIds());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping
    public ResponseEntity<Void> updateGenerationMemberRole(
            @RequestBody @Valid UpdateGenerationMemberRoleRequest request) {
        generationMemberService.updateGenerationMemberRole(request.generationMemberId(), request.role());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteGenerationMember(@RequestBody @Valid DeleteGenerationMemberRequest request) {
        generationMemberService.deleteGenerationMembers(request.generationMemberIds());
        return ResponseEntity.noContent().build();
    }
}
