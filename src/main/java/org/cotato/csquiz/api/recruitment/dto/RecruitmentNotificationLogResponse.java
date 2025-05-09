package org.cotato.csquiz.api.recruitment.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotification;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotificationEmailLog;

public record RecruitmentNotificationLogResponse(
        Long NotificationId,
        LocalDateTime sendTime,
        String sender,
        Long sendCount,
        Long sendSuccess,
        Long sendFail
) {

    public static RecruitmentNotificationLogResponse of(RecruitmentNotification notification,
                                                        List<RecruitmentNotificationEmailLog> logs) {
        long total = logs.size();
        long success = logs.stream()
                .filter(RecruitmentNotificationEmailLog::getSendSuccess)
                .count();
        long fail = total - success;

        return new RecruitmentNotificationLogResponse(
                notification.getId(),
                notification.getSendTime(),
                notification.getSenderName(),
                total,
                success,
                fail
        );
    }
}
