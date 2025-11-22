package org.cotato.csquiz.domain.auth.event;

import org.cotato.csquiz.common.event.CotatoEvent;
import org.cotato.csquiz.common.event.EventType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailSendEvent implements CotatoEvent {

	private EventType type;
	private EmailSendEventDto data;
}
