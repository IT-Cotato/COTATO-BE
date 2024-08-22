package org.cotato.csquiz.api.event.dto;

import java.time.LocalDateTime;

public record TimeRequest(
        LocalDateTime testTime
) {
}
