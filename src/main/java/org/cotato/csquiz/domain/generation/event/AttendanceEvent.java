package org.cotato.csquiz.domain.generation.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.common.event.CotatoEvent;
import org.cotato.csquiz.common.event.EventType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceEvent implements CotatoEvent {

    private EventType type;

    private AttendanceEventDto data;
}
