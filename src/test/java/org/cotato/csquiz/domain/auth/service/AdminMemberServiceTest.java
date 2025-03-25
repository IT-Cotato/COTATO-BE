package org.cotato.csquiz.domain.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.util.List;
import org.cotato.csquiz.common.error.ErrorCode;
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

    @Test
    void 부원을_OM전환() {
        // given
        Member member1 = Member.defaultMember("email", "pwd", "dd", "1");
        Member member2 = Member.defaultMember("email2", "pwd", "dd2", "2");
        member1.updateStatus(MemberStatus.APPROVED);
        member2.updateStatus(MemberStatus.APPROVED);

        when(memberReader.findAllByIdsInWithValidation(anyList())).thenReturn(List.of(member1, member2));

        // when
        adminMemberService.updateToRetireMembers(List.of(1L, 2L));

        // then
        assertEquals(MemberStatus.RETIRED, member1.getStatus());
        assertEquals(MemberStatus.RETIRED, member2.getStatus());
    }

    @Test
    void 개발팀은_OM으로_전환하지_않는다() {
        // given
        Member devTeam = Member.defaultMember("email", "pwd", "dd", "1");
        devTeam.updateRole(MemberRole.DEV);
        devTeam.updateStatus(MemberStatus.APPROVED);

        when(memberReader.findAllByIdsInWithValidation(anyList())).thenReturn(List.of(devTeam));

        // when, then
        AppException exception = assertThrows(AppException.class, () ->
                adminMemberService.updateToRetireMembers(List.of(1L))
        );
        assertEquals(ErrorCode.CANNOT_CHANGE_DEV_ROLE.getMessage(), exception.getErrorCode().getMessage());
    }

    @Test
    void 활동_부원이_아닌_경우_OM전환_에러() {
        // given
        Member member1 = Member.defaultMember("email", "pwd", "dd", "1");
        Member member2 = Member.defaultMember("email2", "pwd", "dd2", "2");
        member1.updateStatus(MemberStatus.INACTIVE);
        member1.updateStatus(MemberStatus.REJECTED);

        when(memberReader.findAllByIdsInWithValidation(anyList())).thenReturn(List.of(member1, member2));

        // when, then
        assertThrows(AppException.class, () -> adminMemberService.updateToRetireMembers(List.of(1L, 2L)));
    }

    @Test
    void OM을_일반_부원으로_전환() {
        // given
        Member member = Member.defaultMember("email", "pwd", "dd", "1");
        member.updateStatus(MemberStatus.RETIRED);

        when(memberReader.findById(any())).thenReturn(member);

        // when
        adminMemberService.updateToApprovedMember(member.getId());

        // then
        assertEquals(MemberStatus.APPROVED, member.getStatus());
    }
}