package org.cotato.csquiz.api.policy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.policy.dto.CheckMemberPoliciesRequest;
import org.cotato.csquiz.api.policy.dto.FindMemberPolicyResponse;
import org.cotato.csquiz.domain.auth.service.PolicyService;
import org.springframework.http.ResponseEntity;
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
    @GetMapping("/essential")
    public ResponseEntity<FindMemberPolicyResponse> getUnCheckedPolicies(@RequestParam(value = "member-id") Long memberId) {
        return ResponseEntity.ok().body(policyService.findUnCheckedPolicies(memberId));
    }

    @Operation(summary = "특정 정책에 대해 동의 여부 체크 API")
    @PostMapping("/check")
    public ResponseEntity<Void> checkPolicies(@RequestBody @Valid CheckMemberPoliciesRequest request){
        policyService.checkPolicies(request);
        return ResponseEntity.noContent().build();
    }
}
