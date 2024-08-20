package org.cotato.csquiz.common.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

    private static final Long DEFAULT_TIMEOUT = 60 * 1000 * 60L;

    private final SseAttendanceRepository sseAttendanceRepository;
    private final SseSender sseSender;

    public SseEmitter subscribeAttendance(final Long memberId) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        setBaseEmitterConfiguration(memberId, sseEmitter);
        sseAttendanceRepository.save(memberId, sseEmitter);

        sseSender.sendInitialAttendanceStatus(sseEmitter);

        return sseEmitter;
    }

    private void setBaseEmitterConfiguration(Long memberId, SseEmitter sseEmitter) {
        sseEmitter.onCompletion(() -> {
            log.info("---- [memberId]: {} on completion callback ----", memberId);
            sseAttendanceRepository.deleteById(memberId);
        });

        sseEmitter.onTimeout(() -> {
            log.info("---- [memberId]: {} on timeout callback ----", memberId);
            sseEmitter.complete();
        });
    }
}
