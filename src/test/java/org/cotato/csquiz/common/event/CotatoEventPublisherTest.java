package org.cotato.csquiz.common.event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class CotatoEventPublisherTest {

    @InjectMocks
    private CotatoEventPublisher cotatoEventPublisher;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    @DisplayName("이벤트 발행 테스트")
    void publishEvent() {
        // given
        CotatoEvent event = mock(CotatoEvent.class);

        // when
        cotatoEventPublisher.publishEvent(event);

        // then
        assertNotNull(event);
        verify(applicationEventPublisher).publishEvent(event);
    }
}