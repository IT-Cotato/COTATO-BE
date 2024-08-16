package org.cotato.csquiz.common.sse;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseService {

    private static final String SUBSCRIBE_ATTENDANCE = "attendance";
    private static final String CONNECTED = "connected";
    private final Map<Long, SseEmitter> attendances = new ConcurrentHashMap<>();

    public SseEmitter subscribeAttendance(final Long memberId) throws IOException {
        SseEmitter sseEmitter = new SseEmitter();

        this.attendances.put(memberId, sseEmitter);

        sseEmitter.send(SseEmitter.event()
                .name(SUBSCRIBE_ATTENDANCE)
                .data(CONNECTED)
                .build());

        sseEmitter.onCompletion(() -> {
            log.info("---- [memberId]: {} on completion callback ----", memberId);
            this.attendances.remove(memberId, sseEmitter);
        });

        sseEmitter.onTimeout(() -> {
            log.info("---- [memberId]: {} on timeout callback ----", memberId);
            sseEmitter.complete();
        });

        return sseEmitter;
    }
}
