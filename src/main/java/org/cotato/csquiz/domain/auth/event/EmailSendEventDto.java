package org.cotato.csquiz.domain.auth.event;

import lombok.Builder;
import org.cotato.csquiz.domain.auth.entity.Member;

@Builder
public record EmailSendEventDto(
		Member member
) {
}
