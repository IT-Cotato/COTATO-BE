package org.cotato.csquiz.common.event;

import org.cotato.csquiz.domain.auth.entity.Member;

public class EmailSendEvent extends CotatoEvent<Member> {

    public EmailSendEvent(EventType type, Member data) {
        super(type, data);
    }
}
