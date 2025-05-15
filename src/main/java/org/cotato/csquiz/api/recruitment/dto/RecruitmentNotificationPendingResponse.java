package org.cotato.csquiz.api.recruitment.dto;

public record RecruitmentNotificationPendingResponse(
        Long notificationCount
) {
    public static RecruitmentNotificationPendingResponse of(Long notificationCount) {
        return new RecruitmentNotificationPendingResponse(notificationCount);
    }
}
