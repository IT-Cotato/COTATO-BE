package org.cotato.csquiz.api.event.controller;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.common.schedule.SchedulerService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {

    private final SchedulerService schedulerService;

    public void enrollTestSchedule(LocalDateTime testTime) {
        schedulerService.scheduleNotification(testTime);
    }
}
