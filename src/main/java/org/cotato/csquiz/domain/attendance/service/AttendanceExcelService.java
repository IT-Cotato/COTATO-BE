package org.cotato.csquiz.domain.attendance.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.poi.ExcelWriter;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.poi.AttendanceRecordExcelData;
import org.cotato.csquiz.domain.attendance.poi.AttendanceRecordExcelData.AttendRecord;
import org.cotato.csquiz.domain.attendance.service.component.AttendanceReader;
import org.cotato.csquiz.domain.attendance.service.component.AttendanceRecordReader;
import org.cotato.csquiz.domain.attendance.util.AttendanceExcelUtil;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.service.component.GenerationMemberReader;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.GenerationMember;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.service.component.GenerationReader;
import org.cotato.csquiz.domain.generation.service.component.SessionReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AttendanceExcelService {

    private final AttendanceRecordService attendanceRecordService;
    private final GenerationReader generationReader;
    private final SessionReader sessionReader;
    private final AttendanceRecordReader attendanceRecordReader;
    private final AttendanceReader attendanceReader;
    private final GenerationMemberReader generationMemberReader;

    public Map<String, Object> getAttendanceRecordsExcelDataByGeneration(final Long generationId) {
        Generation generation = generationReader.findById(generationId);
        Map<Long, Session> sessionById = sessionReader.findAllByGeneration(generation).stream()
                .collect(Collectors.toUnmodifiableMap(Session::getId, Function.identity()));

        List<Attendance> attendances = attendanceReader.getAllBySessions(sessionById.values());
        List<Member> members = generationMemberReader.getAllByGeneration(generation).stream()
                .map(GenerationMember::getMember).toList();

        attendances.forEach(attendanceRecordService::refreshAttendanceRecords);

        List<AttendanceRecordExcelData> excelData = getAttendanceRecordsExcelData(members, attendances, sessionById);

        Map<String, Object> datas = new HashMap<>();
        datas.put(ExcelWriter.FILE_NAME, AttendanceExcelUtil.getGenerationRecordExcelFileName(generation));
        datas.put(ExcelWriter.SHEETS, Map.of(AttendanceExcelUtil.getGenerationRecordExcelFileName(generation), excelData));

        return datas;
    }

    private List<AttendanceRecordExcelData> getAttendanceRecordsExcelData(final List<Member> members,
                                                                          final List<Attendance> attendances,
                                                                          final Map<Long, Session> sessionById) {
        Map<Long, Member> memberById = members.stream()
                .collect(Collectors.toUnmodifiableMap(Member::getId, Function.identity()));

        Map<Long, List<AttendanceRecord>> recordsByMemberId = attendanceRecordReader.getAllByAttendances(attendances).stream()
                .filter(attendanceRecord -> memberById.containsKey(attendanceRecord.getMemberId()))
                .collect(Collectors.groupingBy(AttendanceRecord::getMemberId));

        Map<Long, Session> sessionByAttendanceId = attendances.stream()
                .collect(Collectors.toMap(Attendance::getId, attendance -> sessionById.get(attendance.getSessionId())));

        return recordsByMemberId.entrySet().stream()
                .map(memberIdAndRecords -> {
                    Member member = memberById.get(memberIdAndRecords.getKey());
                    List<AttendRecord> records = memberIdAndRecords.getValue().stream()
                            .map(attendanceRecord -> AttendRecord.of(
                                    sessionByAttendanceId.get(attendanceRecord.getAttendanceId()), attendanceRecord))
                            .toList();

                    return AttendanceRecordExcelData.of(member, attendances.size(), records);
                })
                .toList();
    }

    public Map<String, Object> getAttendanceRecordsExcelDataByAttendanceIds(final List<Long> attendanceIds) {
        List<Attendance> attendances = attendanceReader.getAllByIds(attendanceIds);

        if (attendances.size() != attendanceIds.size()) {
            throw new EntityNotFoundException("출석이 존재하지 않습니다.");
        }

        attendances.forEach(attendanceRecordService::refreshAttendanceRecords);

        List<Long> sessionIds = attendances.stream().map(Attendance::getSessionId).toList();
        List<Session> sessions = sessionReader.findAllByIdIn(sessionIds);
        checkSameGeneration(sessions);

        Session session = sessions.get(0);
        Generation generation = session.getGeneration();

        List<Member> members = generationMemberReader.findAllByGenerationWithMember(generation).stream()
                .map(GenerationMember::getMember)
                .toList();

        Map<Long, Session> sessionById = sessions.stream()
                .collect(Collectors.toUnmodifiableMap(Session::getId, Function.identity()));

        List<AttendanceRecordExcelData> excelData = getAttendanceRecordsExcelData(members, attendances,
                sessionById);

        Map<String, Object> datas = new HashMap<>();
        datas.put(ExcelWriter.FILE_NAME, AttendanceExcelUtil.getAttendanceRecordExcelFileName(attendances, sessionById, generation));
        datas.put(ExcelWriter.SHEETS, Map.of(AttendanceExcelUtil.getAttendanceRecordExcelFileName(attendances, sessionById, generation), excelData));

        return datas;
    }

    private void checkSameGeneration(final List<Session> sessions) {
        Set<Long> generationIds = sessions.stream()
                .map(Session::getGeneration)
                .map(Generation::getId)
                .collect(Collectors.toUnmodifiableSet());
        if (generationIds.size() != 1) {
            throw new AppException(ErrorCode.INVALID_ATTENDANCE_LIST);
        }
    }
}
