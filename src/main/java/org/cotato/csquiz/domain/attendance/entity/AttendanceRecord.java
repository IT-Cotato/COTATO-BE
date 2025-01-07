package org.cotato.csquiz.domain.attendance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.common.entity.BaseTimeEntity;
import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;
import org.cotato.csquiz.domain.auth.entity.Member;

@Table(name = "attendance_record",
        indexes = {@Index(name = "member_id_index", columnList = "member_id")},
        uniqueConstraints = {@UniqueConstraint(columnNames = {"member_id", "attendance_id"})}
)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttendanceRecord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_record_id")
    private Long id;

    @Column(name = "attendance_type")
    @Enumerated(EnumType.STRING)
    private AttendanceType attendanceType;

    @Column(name = "attendance_result", nullable = false)
    @Enumerated(EnumType.STRING)
    private AttendanceResult attendanceResult;

    @Column(name = "location_accuracy")
    private Double locationAccuracy;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "attendance_id", nullable = false)
    private Long attendanceId;

    @Column(name = "attend_time")
    private LocalDateTime attendTime;

    @Builder(access = AccessLevel.PRIVATE)
    private AttendanceRecord(AttendanceType attendanceType, AttendanceResult attendanceResult, Double locationAccuracy,
                             Long memberId, Attendance attendance, LocalDateTime attendTime) {
        this.attendanceType = attendanceType;
        this.attendanceResult = attendanceResult;
        this.locationAccuracy = locationAccuracy;
        this.memberId = memberId;
        this.attendanceId = attendance.getId();
        this.attendTime = attendTime;
    }

    public static AttendanceRecord onLineRecord(Attendance attendance, Long memberId, AttendanceResult attendanceResult,
                                                LocalDateTime attendTime) {
        return AttendanceRecord.builder()
                .attendanceType(AttendanceType.ONLINE)
                .attendanceResult(attendanceResult)
                .memberId(memberId)
                .attendance(attendance)
                .attendTime(attendTime)
                .build();
    }

    public static AttendanceRecord offlineRecord(Attendance attendance, Long memberId, Double locationAccuracy,
                                                 AttendanceResult attendanceResult, LocalDateTime attendTime) {
        return new AttendanceRecord(
                AttendanceType.OFFLINE,
                attendanceResult,
                locationAccuracy,
                memberId,
                attendance,
                attendTime
        );
    }

    public static AttendanceRecord absentRecord(Attendance attendance, Long memberId) {
        return AttendanceRecord.builder()
                .attendanceType(AttendanceType.NO_ATTEND)
                .attendanceResult(AttendanceResult.ABSENT)
                .memberId(memberId)
                .attendance(attendance)
                .build();
    }

    public static AttendanceRecord defaultAttendanceRecord(Attendance attendance, Member member) {
        return AttendanceRecord.builder()
                .attendanceResult(AttendanceResult.ABSENT)
                .memberId(member.getId())
                .attendance(attendance)
                .build();
    }

    public void updateAttendanceType(AttendanceType attendanceType) {
        this.attendanceType = attendanceType;
    }

    public void updateLocationAccuracy(Double accuracy) {
        this.locationAccuracy = accuracy;
    }

    public void updateAttendanceResult(AttendanceResult attendanceResult) {
        this.attendanceResult = attendanceResult;
    }

    public boolean isPresent() {
        return attendanceResult.isPresented();
    }
}
