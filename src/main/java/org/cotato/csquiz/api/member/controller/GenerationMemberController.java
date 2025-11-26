package org.cotato.csquiz.api.member.controller;

import org.cotato.csquiz.api.member.dto.CreateGenerationMemberRequest;
import org.cotato.csquiz.api.member.dto.GenerationMemberInfoResponse;
import org.cotato.csquiz.api.member.dto.UpdateGenerationMemberRoleRequest;
import org.cotato.csquiz.common.role.RoleAuthority;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.domain.auth.service.GenerationMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "기수별 활동 멤버 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/api/generation-members")
public class GenerationMemberController {

	private final GenerationMemberService generationMemberService;

	@Operation(summary = "기수별 활동 멤버 조회")
	@RoleAuthority(MemberRole.ADMIN)
	@GetMapping
	public ResponseEntity<GenerationMemberInfoResponse> findGenerationMember(
		@RequestParam("generationId") Long generationId) {
		return ResponseEntity.ok().body(generationMemberService.findGenerationMemberByGeneration(generationId));
	}

	@Operation(summary = "기수별 활동 멤버 추가")
	@RoleAuthority(MemberRole.ADMIN)
	@PostMapping
	public ResponseEntity<Void> addGenerationMember(@RequestBody @Valid CreateGenerationMemberRequest request) {
		generationMemberService.addGenerationMember(request.generationId(), request.memberIds());
		return ResponseEntity.noContent().build();
	}

	@Operation(
		summary = "기수별 활동 멤버 역할 수정",
		description = "지정된 기수 멤버의 역할을 수정합니다.",
		operationId = "updateGenerationMemberRole",
		parameters = {
			@Parameter(name = "generationMemberId", description = "기수별 활동 멤버 ID",
				required = true, in = ParameterIn.PATH),
		}
	)
	@RoleAuthority(MemberRole.ADMIN)
	@PatchMapping("/{generationMemberId}/role")
	public ResponseEntity<Void> updateGenerationMemberRole(@PathVariable("generationMemberId") Long generationMemberId,
		@RequestBody @Valid UpdateGenerationMemberRoleRequest request) {
		generationMemberService.updateGenerationMemberRole(generationMemberId, request.role());
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "기수별 활동 멤버 삭제")
	@RoleAuthority(MemberRole.ADMIN)
	@DeleteMapping
	public ResponseEntity<Void> deleteGenerationMember(
		@RequestParam(name = "generationMemberId") Long generationMemberId) {
		generationMemberService.deleteGenerationMember(generationMemberId);
		return ResponseEntity.noContent().build();
	}
}
