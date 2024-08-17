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

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final Map<Long, SseEmitter> attendances = new ConcurrentHashMap<>();
    private final SseSender sseSender;

    public SseEmitter subscribeAttendance(final Long memberId) throws IOException {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        saveEmitter(memberId, sseEmitter);
        setBaseEmitterConfiguration(memberId, sseEmitter);

        sseSender.sendAttendanceConnected(sseEmitter);
        sseSender.sendInitialAttendanceStatus(sseEmitter);

        return sseEmitter;
    }

    private void setBaseEmitterConfiguration(Long memberId, SseEmitter sseEmitter) {
        sseEmitter.onCompletion(() -> {
            log.info("---- [memberId]: {} on completion callback ----", memberId);
            this.attendances.remove(memberId, sseEmitter);
        });

        sseEmitter.onTimeout(() -> {
            log.info("---- [memberId]: {} on timeout callback ----", memberId);
            sseEmitter.complete();
        });
    }

    private void saveEmitter(Long memberId, SseEmitter sseEmitter) {
        this.attendances.computeIfPresent(memberId, (key, existing) -> {
            existing.complete();
            return sseEmitter;
        });

        this.attendances.putIfAbsent(memberId, sseEmitter);
    }
}
