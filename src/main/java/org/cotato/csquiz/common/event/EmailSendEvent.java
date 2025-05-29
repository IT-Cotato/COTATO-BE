package org.cotato.csquiz.common.event;

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
