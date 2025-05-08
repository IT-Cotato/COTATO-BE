package org.cotato.csquiz.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;

@Getter
@Builder
@AllArgsConstructor
public class CotatoEvent<T> {

    private final EventType type;
    private final T data;
}
