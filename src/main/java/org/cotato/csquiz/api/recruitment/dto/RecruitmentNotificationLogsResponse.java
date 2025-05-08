package org.cotato.csquiz.api.recruitment.dto;

import java.util.List;

public record RecruitmentNotificationLogsResponse(
        List<RecruitmentNotificationLogDto> notificationLogs
) {
    public static RecruitmentNotificationLogsResponse of(List<RecruitmentNotificationLogDto> dtos) {
        return new RecruitmentNotificationLogsResponse(dtos);
    }
}
