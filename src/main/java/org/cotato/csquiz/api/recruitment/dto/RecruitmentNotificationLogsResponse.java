package org.cotato.csquiz.api.recruitment.dto;

import java.util.List;

public record RecruitmentNotificationLogsResponse(
	List<RecruitmentNotificationLogResponse> notificationLogs
) {
	public static RecruitmentNotificationLogsResponse of(List<RecruitmentNotificationLogResponse> dtos) {
		return new RecruitmentNotificationLogsResponse(dtos);
	}
}
