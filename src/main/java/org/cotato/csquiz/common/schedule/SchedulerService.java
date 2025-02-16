package org.cotato.csquiz.common.schedule;

import jakarta.annotation.PostConstruct;
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
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.service.AttendanceRecordService;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.entity.RefusedMember;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.cotato.csquiz.domain.auth.repository.RefusedMemberRepository;
import org.cotato.csquiz.domain.education.service.EducationService;
import org.cotato.csquiz.domain.generation.entity.AttendanceNotification;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.repository.AttendanceNotificationRepository;
import org.cotato.csquiz.domain.generation.service.component.SessionReader;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SchedulerService {

    private final AttendanceNotificationRepository sessionNotificationRepository;
    private final RefusedMemberRepository refusedMemberRepository;
    private final MemberRepository memberRepository;
    private final EducationService educationService;
    private final AttendanceRecordService attendanceRecordService;
    private final SessionReader sessionReader;
    private final SseSender sseSender;
    private final TaskScheduler taskScheduler;
    private final Map<Long, ScheduledFuture<?>> notificationByAttendanceId = new ConcurrentHashMap<>();

    @PostConstruct
    protected void restoreScheduledTasksFromDB() {
        List<AttendanceNotification> attendanceNotifications = sessionNotificationRepository.findAllByDoneFalse();

        attendanceNotifications.forEach(
                attendanceNotification -> {
                    Session session = sessionReader.findById(attendanceNotification.getAttendance().getSessionId());
                    ScheduledFuture<?> schedule = taskScheduler.schedule(
                            () -> sseSender.sendAttendanceStartNotification(attendanceNotification),
                            TimeUtil.getSeoulZoneTime(session.getSessionDateTime())
                                    .toInstant());
                    notificationByAttendanceId.put(attendanceNotification.getAttendance().getId(), schedule);
                    log.info("restored attendance notification: attendance id <{}>", attendanceNotification.getAttendance().getId());
                });
    }

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

    @Transactional
    public void scheduleAttendanceNotification(final Attendance attendance) {
        AttendanceNotification sessionNotification = AttendanceNotification.builder().attendance(attendance).done(false).build();
        sessionNotificationRepository.save(sessionNotification);

        Session session = sessionReader.findById(attendance.getSessionId());
        ZonedDateTime zonedDateTime = TimeUtil.getSeoulZoneTime(session.getSessionDateTime());

        ScheduledFuture<?> schedule = taskScheduler.schedule(() -> sseSender.sendAttendanceStartNotification(sessionNotification),
                zonedDateTime.toInstant());
        notificationByAttendanceId.put(session.getId(), schedule);
    }

    public void scheduleAbsentRecords(LocalDateTime sessionDateTime, Long sessionId) {
        // 이미 해당 세션에 스케줄된 작업이 있으면 취소
        ScheduledFuture<?> existingTask = notificationByAttendanceId.get(sessionId);
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
                        notificationByAttendanceId.remove(sessionId);
                    }
                },
                zonedDateTime.toInstant()
        );

        notificationByAttendanceId.put(sessionId, newTask);
    }
}
