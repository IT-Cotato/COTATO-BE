package org.cotato.csquiz.domain.attendance.service.component;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRecordRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AttendanceRecordReader {

    private final AttendanceRecordRepository attendanceRecordRepository;

    public List<AttendanceRecord> getAllByAttendances(final List<Attendance> attendances) {
        List<Long> attendanceIds = attendances.stream().map(Attendance::getId).toList();
        return attendanceRecordRepository.findAllByAttendanceIdsInQuery(attendanceIds);
    }

    public boolean isAttendanceRecordExist(final Attendance attendance) {
        return attendanceRecordRepository.existsByAttendanceId(attendance.getId());
    }
}
