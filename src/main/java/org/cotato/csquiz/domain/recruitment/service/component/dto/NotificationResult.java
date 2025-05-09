package org.cotato.csquiz.domain.recruitment.service.component.dto;

public record NotificationResult(
        Long requestId,
        boolean success
) {
    public static NotificationResult of(Long requestId, boolean success) {
        return new NotificationResult(requestId, success);
    }
}
