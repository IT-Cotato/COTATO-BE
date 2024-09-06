package org.cotato.csquiz.domain.auth.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.policy.dto.CheckPolicyRequest;
import org.cotato.csquiz.api.policy.dto.FindMemberPolicyResponse;
import org.cotato.csquiz.api.policy.dto.PoliciesResponse;
import org.cotato.csquiz.api.policy.dto.PolicyInfoResponse;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.entity.MemberPolicy;
import org.cotato.csquiz.domain.auth.entity.Policy;
import org.cotato.csquiz.domain.auth.enums.PolicyType;
import org.cotato.csquiz.domain.auth.repository.MemberPolicyRepository;
import org.cotato.csquiz.domain.auth.repository.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PolicyService {

    private final MemberService memberService;
    private final PolicyRepository policyRepository;
    private final MemberPolicyRepository memberPolicyRepository;

    public FindMemberPolicyResponse findUnCheckedPolicies(final Long memberId) {
        // 회원이 체크한 정책
        List<Long> checkedPolicies = memberPolicyRepository.findAllByMemberId(memberId).stream()
                .filter(MemberPolicy::getIsChecked)
                .map(MemberPolicy::getPolicyId)
                .toList();

        List<PolicyInfoResponse> uncheckedEssentialPolicies = policyRepository.findAllByPolicyType(PolicyType.ESSENTIAL)
                .stream()
                .filter(policy -> !checkedPolicies.contains(policy.getId()))
                .map(PolicyInfoResponse::from)
                .toList();

        List<PolicyInfoResponse> uncheckedOptionalPolicies = policyRepository.findAllByPolicyType(PolicyType.OPTIONAL)
                .stream()
                .filter(policy -> !checkedPolicies.contains(policy.getId()))
                .map(PolicyInfoResponse::from)
                .toList();

        return FindMemberPolicyResponse.of(memberId, uncheckedEssentialPolicies, uncheckedOptionalPolicies);
    }

    /**
     * 이용약관 동의 체크: 부원은 이용약관에 동의 또는 거절을 할 수 있다. 1. 체크하려는 정책이 존재하는지 확인할 것 2. 이미 해당 정책에 체크했는지 확인할 것 3. 필수 동의 정책에 동의하지 않았는지
     * 확인할 것 4. 검증 시 문제가 없으면 정책 동의 처리
     */
    @Transactional
    public void checkPolicies(Long memberId, List<CheckPolicyRequest> policies) {
        Member findMember = memberService.findById(memberId);

        List<Long> policyIds = policies.stream()
                .map(CheckPolicyRequest::policyId)
                .toList();

        if (isAlreadyChecked(findMember, policyIds)) {
            throw new AppException(ErrorCode.ALREADY_POLICY_CHECK);
        }

        Map<Long, Policy> policyMap = policyRepository.findAllByIdIn(policyIds).stream()
                .collect(Collectors.toMap(Policy::getId, Function.identity()));

        List<MemberPolicy> memberPolicies = policies.stream()
                .map(policyRequest -> MemberPolicy.of(policyRequest.isChecked(), findMember,
                        policyMap.get(policyRequest.policyId())))
                .toList();

        if (hasDisagreementInEssential(memberPolicies)) {
            throw new AppException(ErrorCode.SHOULD_AGREE_POLICY);
        }

        memberPolicyRepository.saveAll(memberPolicies);
    }

    private boolean isAlreadyChecked(Member findMember, List<Long> policyIds) {
        return memberPolicyRepository.findAllByMemberId(findMember.getId()).stream()
                .map(MemberPolicy::getPolicyId)
                .anyMatch(policyIds::contains);
    }

    private boolean hasDisagreementInEssential(List<MemberPolicy> checkedPolicies) {
        return checkedPolicies.stream()
                .filter(checkedPolicy -> checkedPolicy.getIsChecked().equals(false))
                .map(MemberPolicy::getPolicy)
                .map(Policy::getPolicyType)
                .anyMatch(PolicyType.ESSENTIAL::equals);
    }

    public PoliciesResponse findPolicies() {
        List<PolicyInfoResponse> policies = policyRepository.findAll().stream()
                .map(PolicyInfoResponse::from)
                .toList();
        return new PoliciesResponse(policies);
    }
}
