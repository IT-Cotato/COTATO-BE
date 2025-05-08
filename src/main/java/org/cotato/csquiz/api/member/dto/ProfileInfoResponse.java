package org.cotato.csquiz.api.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.List;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.entity.ProfileLink;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;

public record ProfileInfoResponse(
        @Schema(description = "멤버 pk", requiredMode = RequiredMode.REQUIRED)
        Long memberId,
        @Schema(description = "멤버 이름", requiredMode = RequiredMode.REQUIRED)
        String name,
        @Schema(description = "합격 기수", requiredMode = RequiredMode.REQUIRED)
        Integer generationNumber,
        @Schema(description = "멤버 포지션", requiredMode = RequiredMode.REQUIRED)
        MemberPosition position,
        @Schema(description = "프로필 사진", requiredMode = RequiredMode.REQUIRED, nullable = true)
        String profileImage,
        @Schema(description = "자기 소개", requiredMode = RequiredMode.REQUIRED, nullable = true)
        String introduction,
        @Schema(description = "대학교", requiredMode = RequiredMode.REQUIRED, nullable = true)
        String university,
        @Schema(description = "프로필 링크", requiredMode = RequiredMode.REQUIRED)
        List<ProfileLinkResponse> profileLinks
) {
    public static ProfileInfoResponse of(final Member member, final List<ProfileLink> profileLinks,
                                         String profileImageUrl) {
        return new ProfileInfoResponse(
                member.getId(),
                member.getName(),
                member.getPassedGenerationNumber(),
                member.getPosition(),
                profileImageUrl,
                member.getIntroduction(),
                member.getUniversity(),
                profileLinks.stream()
                        .map(ProfileLinkResponse::from)
                        .toList()
        );
    }
}
