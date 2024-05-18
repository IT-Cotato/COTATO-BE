package cotato.csquiz.controller.dto.record;

import cotato.csquiz.controller.dto.member.MemberInfo;
import cotato.csquiz.domain.entity.Record;

public record RecordResponse(
        Long recordId,
        Long ticketNumber,
        Long memberId,
        String memberName,
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
