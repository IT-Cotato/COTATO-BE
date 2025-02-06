package org.cotato.csquiz.domain.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.domain.auth.enums.MemberStatus;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.cotato.csquiz.domain.auth.service.component.MemberReader;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.service.component.GenerationReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class AdminMemberServiceTest {

    @InjectMocks
    private AdminMemberService adminMemberService;

    @Mock
    private GenerationReader generationReader;

    @Mock
    private MemberReader memberReader;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private EmailNotificationService emailNotificationService;

    @Test
    void 부원_승인_요청() {
        // given
        Member member = Member.defaultMember("boysoeng@naver.com", "password", "name", "phoneNumber");
        member.updateStatus(MemberStatus.REQUESTED);

        Generation generation = Generation.builder().number(1).build();

        when(generationReader.findById(any())).thenReturn(generation);
        when(memberReader.findById(any())).thenReturn(member);

        // when
        adminMemberService.approveApplicant(member.getId(), MemberPosition.BE, 1L);

        // then
        assertEquals(MemberStatus.APPROVED, member.getStatus());
        assertEquals(1, member.getPassedGenerationNumber());
        assertEquals(MemberPosition.BE, member.getPosition());
    }

    @Test
    void 부원_승인요청_실패() {
        // given
        Member member = Member.defaultMember("boysoeng@naver.com", "password", "name", "phoneNumber");
        member.updateStatus(MemberStatus.INACTIVE);

        Generation generation = Generation.builder().number(1).build();

        when(generationReader.findById(any())).thenReturn(generation);
        when(memberReader.findById(any())).thenReturn(member);

        // when, then
        assertThrows(AppException.class, () -> adminMemberService.approveApplicant(member.getId(), MemberPosition.BE, 1L));
    }

    @Test
    void 부원_역할_변경() {
        // given
        Member member = Member.defaultMember("email", "pwd", "dd", "");
        member.updateRole(MemberRole.MEMBER);
        when(memberReader.findById(any())).thenReturn(member);

        // when
        adminMemberService.updateMemberRole(member.getId(), MemberRole.ADMIN);

        // then
        assertEquals(MemberRole.ADMIN, member.getRole());
    }
}