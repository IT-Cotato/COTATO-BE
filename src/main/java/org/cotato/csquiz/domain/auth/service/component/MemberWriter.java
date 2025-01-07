package org.cotato.csquiz.domain.auth.service.component;

import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.cotato.csquiz.common.s3.S3Uploader;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
@Transactional
public class MemberWriter {
    private static final String PROFILE_BUCKET_DIRECTORY = "profile";

    private final MemberRepository memberRepository;
    private final S3Uploader s3Uploader;

    public void save(final Member member) {
        memberRepository.save(member);
    }

    public void updateProfileImage(final Member member, final MultipartFile profileImage) throws ImageException {
        member.updateProfileImage(s3Uploader.uploadFiles(profileImage, PROFILE_BUCKET_DIRECTORY));
    }

    public void deleteProfileImageIfPresent(final Member member) {
        if (member.getProfileImage() != null) {
            s3Uploader.deleteFile(member.getProfileImage());
            member.updateProfileImage(null);
        }
    }
}
