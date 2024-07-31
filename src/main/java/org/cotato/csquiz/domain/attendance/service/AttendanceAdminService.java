package org.cotato.csquiz.domain.attendance.service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.session.dto.AddSessionRequest.AttendanceDeadLine;
import org.cotato.csquiz.domain.attendance.embedded.Location;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.entity.Attendance.AttendanceBuilder;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRepository;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AttendanceAdminService {

    private final AttendanceRepository attendanceRepository;

    @Transactional
    public void addAttendance(Session session, LocalDate localDate, Location location,
                              AttendanceDeadLine attendanceDeadLine) {

        if (checkAttendanceTimeValid(attendanceDeadLine.startTime(), attendanceDeadLine.endTime())) {
            throw new AppException(ErrorCode.SESSION_DEADLINE_INVALID);
        }

        Attendance attendance = Attendance.builder()
                .session(session)
                .location(location)
                .startTime(LocalDateTime.of(localDate, attendanceDeadLine.startTime()))
                .endTime(LocalDateTime.of(localDate, attendanceDeadLine.endTime()))
                .build();

        attendanceRepository.save(attendance);
    }
}
