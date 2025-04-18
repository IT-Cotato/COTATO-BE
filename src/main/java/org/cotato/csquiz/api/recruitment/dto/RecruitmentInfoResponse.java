package org.cotato.csquiz.api.recruitment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentInformation;

import java.time.LocalDate;

public record RecruitmentInfoResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Boolean isOpened,
        LocalDate startDate,
        LocalDate endDate,
        String recruitmentUrl
) {
    public static RecruitmentInfoResponse closed() {
        return new RecruitmentInfoResponse(false, null, null, null);
    }

    public static RecruitmentInfoResponse opened(RecruitmentInformation information) {
        return new RecruitmentInfoResponse(
                true,
                information.getPeriod().getStartDate(),
                information.getPeriod().getEndDate(),
                information.getRecruitmentUrl()
        );
    }
}
