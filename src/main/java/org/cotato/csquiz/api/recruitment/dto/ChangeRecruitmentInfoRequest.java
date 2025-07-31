package org.cotato.csquiz.api.recruitment.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ChangeRecruitmentInfoRequest(
        @NotNull(message = "수정할 전환 상태를 입력해주세요")
        Boolean isOpened,
        LocalDate startDate,
        LocalDate endDate,
        String recruitmentUrl
) {
}
