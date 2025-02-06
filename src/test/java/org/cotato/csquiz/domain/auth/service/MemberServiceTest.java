package org.cotato.csquiz.domain.auth.service;

import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.entity.MemberLeavingRequest;
import org.cotato.csquiz.domain.auth.enums.MemberStatus;
import org.cotato.csquiz.domain.auth.service.component.MemberLeavingRequestReader;
import org.cotato.csquiz.domain.auth.service.component.MemberReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberLeavingRequestReader memberLeavingRequestReader;

    @Mock
    private MemberReader memberReader;

    @Test
    void 부원_활성화_요청() {
        // given
        Member member = Member.defaultMember("email", "password", "name", null);
        member.updateStatus(MemberStatus.INACTIVE);
        MemberLeavingRequest leavingRequest = MemberLeavingRequest.of(member, LocalDateTime.now());

        when(memberLeavingRequestReader.getLeavingRequestByMember(member))
                .thenReturn(leavingRequest);
        when(memberReader.findById(member.getId()))
                .thenReturn(member);

        // when
        memberService.activateMember(member.getId());

        // then
        Assertions.assertEquals(MemberStatus.APPROVED, member.getStatus());
        Assertions.assertEquals(true, leavingRequest.isReactivated());
    }

    @Test
    void 비활성화_상태가_아니면_활성화가_불가능하다() {
        // given
        Member member = Member.defaultMember("email", "password", "name", null);
        member.updateStatus(MemberStatus.APPROVED);
        MemberLeavingRequest leavingRequest = MemberLeavingRequest.of(member, LocalDateTime.now());

        when(memberLeavingRequestReader.getLeavingRequestByMember(member))
                .thenReturn(leavingRequest);
        when(memberReader.findById(member.getId()))
                .thenReturn(member);

        // when, then
        Assertions.assertThrows(AppException.class, () -> memberService.activateMember(member.getId()));
    }
}