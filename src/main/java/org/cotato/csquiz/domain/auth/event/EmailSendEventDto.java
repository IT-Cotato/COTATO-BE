package org.cotato.csquiz.domain.auth.event;

import org.cotato.csquiz.domain.auth.entity.Member;

import lombok.Builder;

@Builder
public record EmailSendEventDto(
	Member member
) {
}
