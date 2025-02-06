package org.cotato.csquiz.api.policy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.policy.dto.CheckMemberPoliciesRequest;
import org.cotato.csquiz.api.policy.dto.FindMemberPolicyResponse;
import org.cotato.csquiz.api.policy.dto.PoliciesResponse;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.enums.PolicyCategory;
import org.cotato.csquiz.domain.auth.service.PolicyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원 정책 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/api/policies")
public class PolicyController {

    private final PolicyService policyService;

    @Operation(summary = "체크하지 않은 정책 조회 API")
    @GetMapping("/unchecked")
    public ResponseEntity<FindMemberPolicyResponse> getUnCheckedPolicies(@AuthenticationPrincipal Member member,
                                                                         @RequestParam("category") PolicyCategory category) {
        return ResponseEntity.ok().body(policyService.findUnCheckedPolicies(member, category));
    }

    @Operation(summary = "특정 정책에 대해 동의 여부 체크 API")
    @PostMapping("/check")
    public ResponseEntity<Void> checkPolicies(@RequestBody @Valid CheckMemberPoliciesRequest request,
                                              @AuthenticationPrincipal Member member) {
        policyService.checkPolicies(member, request.policies());
        return ResponseEntity.noContent().build();
    }

    @Deprecated(since = "회원 탈퇴 기능 작업 이후")
    @Operation(summary = "회원 가입 시 보여줘야 할 정책 목록 반환 API")
    @GetMapping
    public ResponseEntity<PoliciesResponse> getPolicies() {
        return ResponseEntity.ok().body(policyService.findPolicies());
    }

    @Operation(summary = "특정 카테고리에 맞는 정책 목록 반환 API")
    @GetMapping(params = "category")
    public ResponseEntity<PoliciesResponse> getPolicies(@RequestParam PolicyCategory category) {
        return ResponseEntity.ok().body(policyService.findPolicies(category));
    }
}
