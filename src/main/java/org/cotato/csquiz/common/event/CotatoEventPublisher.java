package org.cotato.csquiz.common.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CotatoEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishEvent(final CotatoEvent event) {
        log.info("Publishing event: {}", event.getClass().getSimpleName());
        applicationEventPublisher.publishEvent(event);
    }
}
