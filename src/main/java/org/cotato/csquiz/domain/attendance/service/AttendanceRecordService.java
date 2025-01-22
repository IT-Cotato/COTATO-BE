package org.cotato.csquiz.domain.attendance.service;

import static org.cotato.csquiz.domain.attendance.util.AttendanceUtil.getAttendanceOpenStatus;

import jakarta.persistence.EntityNotFoundException;
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
import org.cotato.csquiz.api.attendance.dto.AttendanceRecordResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceStatistic;
import org.cotato.csquiz.api.attendance.dto.GenerationMemberAttendanceRecordResponse;
import org.cotato.csquiz.api.attendance.dto.MemberAttendResponse;
import org.cotato.csquiz.api.attendance.dto.MemberAttendanceRecordsResponse;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.enums.AttendanceOpenStatus;
import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRecordRepository;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRepository;
import org.cotato.csquiz.domain.attendance.util.AttendanceUtil;
import org.cotato.csquiz.domain.auth.component.GenerationMemberAuthValidator;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.service.component.MemberReader;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.enums.SessionType;
import org.cotato.csquiz.domain.generation.service.component.GenerationReader;
import org.cotato.csquiz.domain.generation.service.component.SessionReader;
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
    private final MemberReader memberReader;
    private final GenerationReader generationReader;
    private final SessionReader sessionReader;
    private final GenerationMemberAuthValidator authValidator;

    public List<GenerationMemberAttendanceRecordResponse> findAttendanceRecords(Long generationId) {
        Generation generation = generationReader.findById(generationId);
        List<Long> sessionIds = sessionReader.findAllByGeneration(generation).stream().map(Session::getId).toList();
        List<Attendance> attendances = attendanceRepository.findAllBySessionIdsInQuery(sessionIds);


        List<Long> attendanceIds = attendances.stream().map(Attendance::getId).toList();

        Map<Long, List<AttendanceRecord>> recordsByMemberId = attendanceRecordRepository.findAllByAttendanceIdsInQuery(attendanceIds).stream()
                .collect(Collectors.groupingBy(AttendanceRecord::getMemberId));

        return memberReader.findAllGenerationMember(generation).stream()
                .sorted(Comparator.comparing(Member::getName))
                .map(member -> GenerationMemberAttendanceRecordResponse.of(
                        member,
                        AttendanceStatistic.of(recordsByMemberId.getOrDefault(member.getId(), List.of()), attendances.size())
                ))
                .toList();
    }

    public List<AttendanceRecordResponse> findAttendanceRecordsByAttendance(Long attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new EntityNotFoundException("해당 출석이 존재하지 않습니다"));
        Session session = sessionReader.findById(attendance.getSessionId());

        Map<Long, Member> memberById = memberReader.findAllGenerationMember(session.getGeneration()).stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));

        Map<Long, AttendanceResult> attendanceResultByMemberId = attendanceRecordRepository.findAllByAttendanceIdAndMemberIdIn(
                        attendance.getId(), memberById.keySet().stream().toList()).stream()
                .collect(Collectors.toMap(AttendanceRecord::getMemberId, AttendanceRecord::getAttendanceResult));

        return memberById.keySet().stream()
                .sorted(Comparator.comparing(memberId -> memberById.get(memberId).getName()))
                .map(memberId -> AttendanceRecordResponse.of(memberById.get(memberId), attendanceResultByMemberId.getOrDefault(memberId, null)))
                .toList();
    }

    @Transactional
    public AttendResponse submitRecord(AttendanceParams request, final Member member) {
        Attendance attendance = attendanceRepository.findById(request.attendanceId())
                .orElseThrow(() -> new EntityNotFoundException("해당 출석이 존재하지 않습니다."));

        Session session = sessionReader.findById(attendance.getSessionId());

        authValidator.checkGenerationPermission(member, session.getGeneration());

        // 해당 출석에 출결 입력이 가능한지 확인하는 과정
        if (getAttendanceOpenStatus(session.getSessionDateTime(), attendance, request.requestTime())
                == AttendanceOpenStatus.CLOSED) {
            throw new AppException(ErrorCode.ATTENDANCE_NOT_OPEN);
        }

        // 기존 출결 데이터가 존재하는지 확인
        if (attendanceRecordRepository.existsByAttendanceIdAndMemberIdAndAttendanceType(request.attendanceId(),
                member.getId(), request.attendanceType())) {
            throw new AppException(ErrorCode.ALREADY_ATTEND);
        }

        return requestAttendanceService.attend(request, session, member.getId(), attendance);
    }

    public MemberAttendanceRecordsResponse findAllRecordsBy(final Long generationId, final Member member) {
        Generation generation = generationReader.findById(generationId);
        List<Session> sessions = sessionReader.findAllByGeneration(generation);

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
                        attendanceIds, member.getId()).stream()
                .collect(Collectors.toUnmodifiableMap(AttendanceRecord::getAttendanceId, Function.identity()));

        Map<Boolean, List<Attendance>> recordedAttendance = attendances.stream()
                .collect(Collectors.partitioningBy(at -> attendanceRecordMap.containsKey(at.getId())));

        List<MemberAttendResponse> responses = recordedAttendance.get(true).stream()
                .map(at -> MemberAttendResponse.recordedAttendance(sessionMap.get(at.getSessionId()), at,
                        attendanceRecordMap.get(at.getId())))
                .collect(Collectors.toList());

        responses.addAll(recordedAttendance.get(false).stream()
                .map(at -> MemberAttendResponse.unrecordedAttendance(sessionMap.get(at.getSessionId()), at, member.getId()))
                .toList());

        return MemberAttendanceRecordsResponse.of(generationId, responses);
    }

    @Transactional
    public void updateUnrecordedAttendanceRecord(Long sessionId) {
        Attendance attendance = attendanceRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("해당 세션에 대한 출석이 생성되지 않았습니다."));
        Session session = sessionReader.findById(sessionId);
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

    @Transactional
    public void updateAttendanceRecords(Long attendanceId, Long memberId, AttendanceResult attendanceResult) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new EntityNotFoundException("해당 출석이 존재하지 않습니다"));

        AttendanceRecord attendanceRecord = attendanceRecordRepository.findByMemberIdAndAttendanceId(memberId, attendanceId)
                .orElseGet(() -> AttendanceRecord.absentRecord(attendance, memberId));
        Session session = sessionReader.findById(attendance.getSessionId());

//        if (!session.getSessionType().isSameType(attendanceRecord.getAttendanceType())) {
//            throw new AppException(ErrorCode.INVALID_RECORD_UPDATE);
//        }
        // Todo https://github.com/IT-Cotato/COTATO-BE/issues/204
        attendanceRecord.updateAttendanceResult(attendanceResult);

        attendanceRecordRepository.save(attendanceRecord);
    }

    @Transactional
    public void refreshAttendanceRecords(final Attendance attendance) {
        Session session = sessionReader.findById(attendance.getSessionId());
        if (session.getSessionType() == SessionType.NO_ATTEND) {
            return;
        }

        List<AttendanceRecord> attendanceRecords = attendanceRecordRepository.findAllByAttendanceId(attendance.getId());
        Set<Long> attendedMemberIds = attendanceRecords.stream()
                .map(AttendanceRecord::getMemberId)
                .collect(Collectors.toSet());


        List<AttendanceRecord> newRecords = memberReader.findAllGenerationMember(session.getGeneration()).stream()
                .map(Member::getId)
                .filter(memberId -> !attendedMemberIds.contains(memberId))
                .map(memberId -> AttendanceRecord.absentRecord(attendance, memberId))
                .toList();

        attendanceRecordRepository.saveAll(newRecords);
    }
}
