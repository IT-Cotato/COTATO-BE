package org.cotato.csquiz.domain.auth.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.cotato.csquiz.api.member.dto.ProfileLinkRequest;
import org.cotato.csquiz.common.entity.S3Info;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.cotato.csquiz.common.s3.S3Uploader;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.entity.MemberLeavingRequest;
import org.cotato.csquiz.domain.auth.enums.ImageUpdateStatus;
import org.cotato.csquiz.domain.auth.enums.MemberStatus;
import org.cotato.csquiz.domain.auth.enums.UrlType;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.cotato.csquiz.domain.auth.repository.ProfileLinkRepository;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

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

    @Mock
    private S3Uploader s3Uploader;

    @Mock
    private ProfileLinkRepository profileLinkRepository;

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

    @Test
    void 프로필_정보_업데이트() throws ImageException {
        //given
        Member member = getDefaultMember();
        String introduction = "새로운 소개";
        String university = "새로운 대학교";
        List<ProfileLinkRequest> profileLinkRequests = List.of(
                new ProfileLinkRequest(UrlType.GITHUB, "https://github.com/user"));
        MockMultipartFile profileImage = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[10]);

        S3Info newS3Info = S3Info.builder()
                .url("new URL")
                .fileName("new file")
                .folderName("new folder")
                .build();
        when(s3Uploader.uploadFiles((MultipartFile) any(), any())).
                thenReturn(newS3Info);

        //when
        memberService.updateMemberProfileInfo(member, introduction, university, profileLinkRequests,
                ImageUpdateStatus.UPDATE, profileImage);

        //then
        Assertions.assertEquals(member.getIntroduction(), introduction);
        Assertions.assertEquals(member.getUniversity(), university);
        Assertions.assertEquals(member.getProfileImage(), newS3Info);
    }

    private Member getDefaultMember() {
        Member member = Member.defaultMember("email", "password", "name", null);
        member.updateStatus(MemberStatus.APPROVED);
        member.updateIntroduction("before");
        member.updateUniversity("before");
        member.updateProfileImage(new S3Info("url", "file", "folder"));
        return member;
    }
}