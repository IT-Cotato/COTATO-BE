package org.cotato.csquiz.domain.attendance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.common.entity.BaseTimeEntity;
import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;

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

    @Column(name = "attendance_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AttendanceType attendanceType;

    @Column(name = "attendance_result", nullable = false)
    @Enumerated(EnumType.STRING)
    private AttendanceResult attendanceResult;

    @Column(name = "location_accuracy")
    private Double locationAccuracy;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id")
    private Attendance attendance;

    private AttendanceRecord(AttendanceType attendanceType, AttendanceResult attendanceResult, Double locationAccuracy,
                             Long memberId, Attendance attendance) {
        this.attendanceType = attendanceType;
        this.attendanceResult = attendanceResult;
        this.locationAccuracy = locationAccuracy;
        this.memberId = memberId;
        this.attendance = attendance;
    }

    public static AttendanceRecord onLineRecord(Attendance attendance, Long memberId,
                                                AttendanceResult attendanceResult) {
        return new AttendanceRecord(
                AttendanceType.ONLINE,
                attendanceResult,
                null,
                memberId,
                attendance
        );
    }

    public static AttendanceRecord offlineRecord(Attendance attendance, Long memberId, Double locationAccuracy,
                                                 AttendanceResult attendanceResult) {
        return new AttendanceRecord(
                AttendanceType.OFFLINE,
                attendanceResult,
                locationAccuracy,
                memberId,
                attendance
        );
    }

    public void updateAttendanceType(AttendanceType attendanceType) {
        this.attendanceType = attendanceType;
    }

    public void updateLocationAccuracy(Double accuracy) {
        this.locationAccuracy = accuracy;
    }
}
