package org.cotato.csquiz.domain.auth.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
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
import org.cotato.csquiz.domain.auth.enums.PolicyCategory;
import org.cotato.csquiz.domain.auth.enums.PolicyType;
import org.cotato.csquiz.domain.auth.repository.MemberPolicyRepository;
import org.cotato.csquiz.domain.auth.repository.PolicyRepository;
import org.cotato.csquiz.domain.auth.service.component.PolicyReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyReader policyReader;
    private final PolicyRepository policyRepository;
    private final MemberPolicyRepository memberPolicyRepository;

    public FindMemberPolicyResponse findUnCheckedPolicies(final Member member, final PolicyCategory category) {
        List<Long> checkedPolicies = memberPolicyRepository.findAllByMemberId(member.getId()).stream()
                .filter(MemberPolicy::getIsChecked)
                .map(MemberPolicy::getPolicyId)
                .toList();

        Map<PolicyType, List<PolicyInfoResponse>> policiesByType = policyReader.getPoliciesByCategory(category).stream()
                .filter(policy -> !checkedPolicies.contains(policy.getId()))
                .map(PolicyInfoResponse::from)
                .collect(Collectors.groupingBy(PolicyInfoResponse::type));

        return FindMemberPolicyResponse.of(member, policiesByType.getOrDefault(PolicyType.ESSENTIAL, List.of()), policiesByType.getOrDefault(PolicyType.OPTIONAL, List.of()));
    }

    @Transactional
    public void checkPolicies(final Member member, List<CheckPolicyRequest> checkedPolicies) {
        List<Long> policyIds = checkedPolicies.stream()
                .map(CheckPolicyRequest::policyId)
                .toList();

        List<Policy> policies = policyRepository.findAllByIdIn(policyIds);

        if (policies.size() != policyIds.size()) {
            throw new EntityNotFoundException("해당 정책을 찾을 수 없습니다.");
        }
        // 해당 정책에 이미 체크했는지 확인
        if (isAlreadyChecked(member, policyIds)) {
            throw new AppException(ErrorCode.ALREADY_POLICY_CHECK);
        }
        // 필수 정책에 체크하지 않았는지 확인
        validateCheckEssentialPolicies(getEssentialPolicies(policies), policyIds);

        List<MemberPolicy> memberPolicies = checkedPolicies.stream()
                .map(policyRequest -> MemberPolicy.of(policyRequest.isChecked(), member, policyRequest.policyId()))
                .toList();

        memberPolicyRepository.saveAll(memberPolicies);
    }

    private void validateCheckEssentialPolicies(List<Policy> essentialPolicies, List<Long> checkedPolicyIds) {
        Set<Long> essentialIds = essentialPolicies.stream()
                .map(Policy::getId)
                .collect(Collectors.toUnmodifiableSet());

        if (checkedPolicyIds.stream().anyMatch(id -> !essentialIds.contains(id))) {
            throw new AppException(ErrorCode.SHOULD_AGREE_POLICY);
        }
    }

    private List<Policy> getEssentialPolicies(List<Policy> policies) {
        return policies.stream()
                .filter(policy -> policy.getPolicyType() == PolicyType.ESSENTIAL)
                .toList();
    }

    private boolean isAlreadyChecked(Member findMember, List<Long> policyIds) {
        return memberPolicyRepository.findAllByMemberId(findMember.getId()).stream()
                .map(MemberPolicy::getPolicyId)
                .anyMatch(policyIds::contains);
    }

    public PoliciesResponse findPolicies() {
        List<PolicyInfoResponse> policies = policyRepository.findAll().stream()
                .map(PolicyInfoResponse::from)
                .toList();
        return new PoliciesResponse(policies);
    }

    public PoliciesResponse findPolicies(PolicyCategory category) {
        List<PolicyInfoResponse> policies = policyRepository.findAllByCategory(category).stream()
                .map(PolicyInfoResponse::from)
                .toList();
        return new PoliciesResponse(policies);
    }
}
