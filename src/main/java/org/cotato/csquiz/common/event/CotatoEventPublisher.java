package org.cotato.csquiz.common.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CotatoEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public <T> void publishEvent(CotatoEvent<T> event) {
        applicationEventPublisher.publishEvent(event);
    }
}
