package org.cotato.csquiz.domain.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.List;
import org.cotato.csquiz.api.policy.dto.FindMemberPolicyResponse;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.entity.Policy;
import org.cotato.csquiz.domain.auth.enums.PolicyCategory;
import org.cotato.csquiz.domain.auth.enums.PolicyType;
import org.cotato.csquiz.domain.auth.repository.MemberPolicyRepository;
import org.cotato.csquiz.domain.auth.service.component.PolicyReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class PolicyServiceTest {

    @InjectMocks
    private PolicyService policyService;

    @Mock
    private PolicyReader policyReader;

    @Mock
    private MemberPolicyRepository memberPolicyRepository;

    @Test
    void 체크하지_않은_정책_조회() {
        // given
        Member member = Member.defaultMember("test", "test", "test", "test");
        Policy policy = Policy.builder().policyType(PolicyType.ESSENTIAL).build();
        when(memberPolicyRepository.findAllByMemberId(member.getId())).thenReturn(List.of());
        when(policyReader.getPoliciesByCategory(PolicyCategory.PERSONAL_INFORMATION)).thenReturn(List.of(policy));

        // when
        FindMemberPolicyResponse unCheckedPolicies = policyService.findUnCheckedPolicies(member,
                PolicyCategory.PERSONAL_INFORMATION);

        // then
        assertEquals(1, unCheckedPolicies.essentialPolicies().size());
    }
}