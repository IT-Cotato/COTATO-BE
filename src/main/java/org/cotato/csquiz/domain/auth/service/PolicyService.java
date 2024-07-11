package org.cotato.csquiz.domain.auth.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.policy.dto.FindMemberPolicyResponse;
import org.cotato.csquiz.api.policy.dto.PolicyInfoResponse;
import org.cotato.csquiz.domain.auth.entity.MemberPolicy;
import org.cotato.csquiz.domain.auth.enums.PolicyType;
import org.cotato.csquiz.domain.auth.repository.MemberPolicyRepository;
import org.cotato.csquiz.domain.auth.repository.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final MemberPolicyRepository memberPolicyRepository;

    public FindMemberPolicyResponse findUnCheckedPolicies(final Long memberId) {
        // 회원이 체크한 정책
        List<Long> checkedPolicies = memberPolicyRepository.findAllByMemberId(memberId).stream()
                .filter(MemberPolicy::getIsChecked)
                .map(MemberPolicy::getId)
                .toList();

        List<PolicyInfoResponse> uncheckedEssentialPolicies = policyRepository.findAllByPolicyType(PolicyType.ESSENTIAL)
                .stream()
                .filter(policy -> !checkedPolicies.contains(policy.getId()))
                .map(PolicyInfoResponse::from)
                .toList();

        List<PolicyInfoResponse> uncheckedOptionalPolicies = policyRepository.findAllByPolicyType(PolicyType.OPTIONAL).stream()
                .filter(policy -> !checkedPolicies.contains(policy.getId()))
                .map(PolicyInfoResponse::from)
                .toList();

        return FindMemberPolicyResponse.of(memberId, uncheckedEssentialPolicies, uncheckedOptionalPolicies);
    }
}
