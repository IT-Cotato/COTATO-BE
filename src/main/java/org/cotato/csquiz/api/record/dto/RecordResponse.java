package org.cotato.csquiz.api.record.dto;

import org.cotato.csquiz.api.member.dto.MemberInfo;
import org.cotato.csquiz.domain.education.entity.Record;

public record RecordResponse(
	Long recordId,
	Long ticketNumber,
	Long memberId,
	String name,
	String backFourNumber,
	String reply
) {

	public static RecordResponse of(Record record, MemberInfo memberInfo) {
		return new RecordResponse(
			record.getId(),
			record.getTicketNumber(),
			memberInfo.memberId(),
			memberInfo.name(),
			memberInfo.backFourNumber(),
			record.getReply()
		);
	}
}
