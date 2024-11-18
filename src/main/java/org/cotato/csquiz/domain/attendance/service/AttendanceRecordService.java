package org.cotato.csquiz.domain.attendance.service;

import static org.cotato.csquiz.domain.attendance.util.AttendanceUtil.getAttendanceOpenStatus;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.attendance.dto.AttendResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceParams;
import org.cotato.csquiz.api.attendance.dto.GenerationMemberAttendanceRecordResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceStatistic;
import org.cotato.csquiz.api.attendance.dto.MemberAttendResponse;
import org.cotato.csquiz.api.attendance.dto.MemberAttendanceRecordsResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceRecordResponse;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.enums.AttendanceOpenStatus;
import org.cotato.csquiz.domain.attendance.enums.AttendanceRecordResult;
import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRecordRepository;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRepository;
import org.cotato.csquiz.domain.attendance.util.AttendanceUtil;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.service.component.MemberReader;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.repository.GenerationMemberRepository;
import org.cotato.csquiz.domain.generation.repository.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceRecordService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final AttendanceRepository attendanceRepository;
    private final RequestAttendanceService requestAttendanceService;
    private final SessionRepository sessionRepository;
    private final MemberReader memberReader;
    private final GenerationMemberRepository generationMemberRepository;

    public List<GenerationMemberAttendanceRecordResponse> generateAttendanceResponses(List<Attendance> attendances, Generation generation) {
        List<Long> attendanceIds = attendances.stream().map(Attendance::getId).toList();

        Map<Long, List<AttendanceRecord>> recordsByMemberId = attendanceRecordRepository.findAllByAttendanceIdsInQuery(attendanceIds).stream()
                .collect(Collectors.groupingBy(AttendanceRecord::getMemberId));

        return memberReader.findAllGenerationMember(generation).stream()
                .sorted(Comparator.comparing(Member::getName))
                .map(member -> GenerationMemberAttendanceRecordResponse.of(
                        member,
                        AttendanceStatistic.of(recordsByMemberId.getOrDefault(member.getId(), List.of()),
                                attendances.size()
                        )
                ))
                .toList();
    }

    public List<AttendanceRecordResponse> generateSingleAttendanceResponses(Attendance attendance,
                                                                            Generation generation) {
        Map<Long, AttendanceRecord> recordByMemberId = attendanceRecordRepository.findAllByAttendanceId(
                        attendance.getId())
                .stream()
                .collect(Collectors.toMap(
                        AttendanceRecord::getMemberId,
                        Function.identity()
                ));
        return memberReader.findAllGenerationMember(generation).stream()
                .sorted(Comparator.comparing(Member::getName))
                .map(member -> AttendanceRecordResponse.of(
                        member,
                        attendanceRecordToRecordResult(recordByMemberId.getOrDefault(member.getId(), null))
                ))
                .toList();
    }

    //AttendanceRecord의 출석정보가 AttendanceRecordResult로 바뀌면 로직 수정 TODO
    private AttendanceRecordResult attendanceRecordToRecordResult(AttendanceRecord record) {
        if (record == null){
            return AttendanceRecordResult.ABSENT;
        }
        return AttendanceRecordResult.convertWithTypeAndResult(
                record.getAttendanceType(),
                record.getAttendanceResult());
    }

    @Transactional
    public AttendResponse submitRecord(AttendanceParams request, final Long memberId) {
        Attendance attendance = attendanceRepository.findById(request.attendanceId())
                .orElseThrow(() -> new EntityNotFoundException("해당 출석이 존재하지 않습니다."));

        Session session = sessionRepository.findById(attendance.getSessionId())
                .orElseThrow(() -> new EntityNotFoundException("해당 출석에 대한 세션이 존재하지 않습니다."));
        Member member = memberReader.findById(memberId);

        checkIsGenerationMember(member, session.getGeneration());

        // 해당 출석에 출결 입력이 가능한지 확인하는 과정
        if (getAttendanceOpenStatus(session.getSessionDateTime(), attendance, request.requestTime())
                == AttendanceOpenStatus.CLOSED) {
            throw new AppException(ErrorCode.ATTENDANCE_NOT_OPEN);
        }

        // 기존 출결 데이터가 존재하는지 확인
        if (attendanceRecordRepository.existsByAttendanceIdAndMemberIdAndAttendanceType(request.attendanceId(),
                memberId, request.attendanceType())) {
            throw new AppException(ErrorCode.ALREADY_ATTEND);
        }

        return requestAttendanceService.attend(request, session.getSessionDateTime(), memberId, attendance);
    }

    private void checkIsGenerationMember(Member member, Generation generation) {
        if (!generationMemberRepository.existsByGenerationAndMember(generation, member)) {
            throw new AppException(ErrorCode.ATTENDANCE_PERMISSION);
        }
    }

    public MemberAttendanceRecordsResponse findAllRecordsBy(final Long generationId, final Long memberId) {
        List<Session> sessions = sessionRepository.findAllByGenerationId(generationId);

        Map<Long, Session> sessionMap = sessions.stream()
                .collect(Collectors.toUnmodifiableMap(Session::getId, Function.identity()));

        List<Long> sessionIds = sessions.stream()
                .map(Session::getId)
                .toList();

        List<Attendance> attendances = attendanceRepository.findAllBySessionIdsInQuery(sessionIds);

        List<Long> attendanceIds = attendances.stream()
                .map(Attendance::getId)
                .toList();

        Map<Long, AttendanceRecord> attendanceRecordMap = attendanceRecordRepository.findAllByAttendanceIdsInQueryAndMemberId(
                        attendanceIds, memberId).stream()
                .collect(Collectors.toUnmodifiableMap(AttendanceRecord::getAttendanceId, Function.identity()));

        Map<Boolean, List<Attendance>> recordedAttendance = attendances.stream()
                .collect(Collectors.partitioningBy(at -> attendanceRecordMap.containsKey(at.getId())));

        List<MemberAttendResponse> responses = recordedAttendance.get(true).stream()
                .map(at -> MemberAttendResponse.recordedAttendance(sessionMap.get(at.getSessionId()), at,
                        attendanceRecordMap.get(at.getId())))
                .collect(Collectors.toList());

        responses.addAll(recordedAttendance.get(false).stream()
                .map(at -> MemberAttendResponse.unrecordedAttendance(sessionMap.get(at.getSessionId()), at, memberId))
                .toList());

        return MemberAttendanceRecordsResponse.of(generationId, responses);
    }

    @Transactional
    public void updateAttendanceStatus(LocalDateTime sessionStartTime, Attendance attendance) {
        List<AttendanceRecord> attendanceRecords = attendanceRecordRepository.findAllByAttendanceId(attendance.getId());

        for (AttendanceRecord attendanceRecord : attendanceRecords) {
            AttendanceResult attendanceResult = AttendanceUtil.calculateAttendanceStatus(sessionStartTime, attendance,
                    attendanceRecord.getAttendTime());
            attendanceRecord.updateAttendanceResult(attendanceResult);
        }

        attendanceRecordRepository.saveAll(attendanceRecords);
    }

    @Transactional
    public void updateUnrecordedAttendanceRecord(Long sessionId) {
        Attendance attendance = attendanceRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("해당 세션에 대한 출석이 생성되지 않았습니다."));
        Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new EntityNotFoundException("해당 세션을 찾을 수 없습니다."));
        // 출결 입력을 한 부원
        Set<Long> attendedMember = attendanceRecordRepository.findAllByAttendanceId(attendance.getId()).stream()
                .map(AttendanceRecord::getMemberId)
                .collect(Collectors.toUnmodifiableSet());

        List<AttendanceRecord> unrecordedMemberIds = memberReader.findAllGenerationMember(session.getGeneration()).stream()
                .map(Member::getId)
                .filter(id -> !attendedMember.contains(id))
                .map(id -> AttendanceRecord.absentRecord(attendance, id))
                .toList();

        attendanceRecordRepository.saveAll(unrecordedMemberIds);
    }
}
