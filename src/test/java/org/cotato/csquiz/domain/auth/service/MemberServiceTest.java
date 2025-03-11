package org.cotato.csquiz.domain.auth.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.entity.MemberLeavingRequest;
import org.cotato.csquiz.domain.auth.enums.MemberStatus;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.cotato.csquiz.domain.auth.service.component.MemberLeavingRequestReader;
import org.cotato.csquiz.domain.auth.service.component.MemberReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private EncryptService encryptService;

    @Mock
    private MemberLeavingRequestReader memberLeavingRequestReader;

    @Mock
    private MemberReader memberReader;

    @Mock
    private MemberRepository memberRepository;

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

    @Test
    void 상태에_따른_부원_목록_조회() {
        // given
        Member member1 = Member.defaultMember("email1", "password1", "name1", "1");
        Member member2 = Member.defaultMember("email2", "password2", "name2", "2");
        member1.updateStatus(MemberStatus.APPROVED);
        member2.updateStatus(MemberStatus.APPROVED);

        PageImpl<Member> members = new PageImpl<>(List.of(member1, member2));
        when(memberRepository.findAllByStatus(MemberStatus.APPROVED, Pageable.ofSize(2))).thenReturn(members);
        when(encryptService.decryptPhoneNumber(any())).thenReturn("01012345678");

        // when
        var approvedMembers = memberService.getMembersByStatus(MemberStatus.APPROVED, Pageable.ofSize(2));

        // then
        Assertions.assertEquals(2, approvedMembers.getNumberOfElements());
        verify(memberRepository).findAllByStatus(MemberStatus.APPROVED, Pageable.ofSize(2));
    }
}