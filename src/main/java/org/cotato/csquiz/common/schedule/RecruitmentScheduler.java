package org.cotato.csquiz.common.schedule;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ScheduledFuture;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.common.util.TimeUtil;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentInformation;
import org.cotato.csquiz.domain.recruitment.service.component.RecruitmentInformationReader;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecruitmentScheduler {

    private final TaskScheduler taskScheduler;
    private final RecruitmentInformationReader recruitmentInformationReader;

    private ScheduledFuture<?> closeTask;

    @PostConstruct
    public void restoreScheduler() {
        RecruitmentInformation info = recruitmentInformationReader.findRecruitmentInformation();
        if (info.isOpened()) {
            scheduleCloseTask(info.endDate());
        }
    }

    @Transactional(readOnly = true)
    public void scheduleCloseTask(LocalDate endDate) {
        LocalDateTime scheduleTime = LocalDateTime.of(endDate.plusDays(1), LocalTime.MIDNIGHT);

        closeTask = taskScheduler.schedule(
                () -> {
                    RecruitmentInformation info = recruitmentInformationReader.findRecruitmentInformation();
                    if (info.isOpened()) {
                        info.changeOpened(false);
                    }
                },
                TimeUtil.getSeoulZoneTime(scheduleTime).toInstant()
        );
    }

    public void cancelTask() {
        if (closeTask != null && !closeTask.isDone()) {
            closeTask.cancel(false);
        }
    }
}
