package org.cotato.csquiz.common.schedule;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.common.sse.SseSender;
import org.cotato.csquiz.common.util.TimeUtil;
import org.cotato.csquiz.domain.attendance.service.AttendanceRecordService;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.entity.RefusedMember;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.cotato.csquiz.domain.auth.repository.RefusedMemberRepository;
import org.cotato.csquiz.domain.education.service.EducationService;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SchedulerService {

    private final RefusedMemberRepository refusedMemberRepository;
    private final MemberRepository memberRepository;
    private final EducationService educationService;
    private final AttendanceRecordService attendanceRecordService;
    private final SseSender sseSender;
    private final TaskScheduler taskScheduler;
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void updateRefusedMember() {
        log.info("updateRefusedMember 시작 {}", LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();
        List<RefusedMember> deleteRefusedMembers = refusedMemberRepository.findAllByCreatedAtBefore(now.minusDays(30));

        List<Member> refusedMembers = new ArrayList<>();
        deleteRefusedMembers.forEach(refusedMember -> {
            if (refusedMember.getMember().isRejectedMember()) {
                refusedMembers.add(refusedMember.getMember());
            }
        });

        memberRepository.deleteAll(refusedMembers);
        refusedMemberRepository.deleteAll(deleteRefusedMembers);
    }

    @Scheduled(cron = "0 0 2 * * SAT")
    public void closeAllCsQuiz() {
        educationService.closeAllFlags();
        log.info("[ CS 퀴즈 모두 닫기 Scheduler 완료 ]");
    }

    public void scheduleSessionNotification(LocalDateTime notificationTime) {
        ZonedDateTime zonedDateTime = TimeUtil.getSeoulZoneTime(notificationTime);

        taskScheduler.schedule(() -> sseSender.sendNotification(notificationTime), zonedDateTime.toInstant());
    }

    public void scheduleAbsentRecords(LocalDateTime sessionDateTime, Long sessionId) {
        // 이미 해당 세션에 스케줄된 작업이 있으면 취소
        ScheduledFuture<?> existingTask = scheduledTasks.get(sessionId);
        if (existingTask != null && !existingTask.isDone()) {
            existingTask.cancel(false);
        }

        LocalDateTime nextDateTime = sessionDateTime.plusDays(1);
        ZonedDateTime zonedDateTime = TimeUtil.getSeoulZoneTime(nextDateTime);

        // 새로운 작업 스케줄링
        ScheduledFuture<?> newTask = taskScheduler.schedule(
                () -> {
                    try {
                        attendanceRecordService.updateUnrecordedAttendanceRecord(sessionId);
                    } finally {
                        scheduledTasks.remove(sessionId);
                    }
                },
                zonedDateTime.toInstant()
        );

        scheduledTasks.put(sessionId, newTask);
    }
}
