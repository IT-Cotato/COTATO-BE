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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.common.entity.BaseTimeEntity;
import org.cotato.csquiz.domain.attendance.enums.AttendanceStatus;
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;

@Table(name = "attendance_record", indexes = {@Index(name = "member_id_index", columnList = "member_id")})
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

    @Column(name = "attendance_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AttendanceStatus attendanceStatus;

    @Column(name = "location_accuracy")
    private Double locationAccuracy;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id")
    private Attendance attendance;

    private AttendanceRecord(AttendanceType attendanceType, AttendanceStatus attendanceStatus, Double locationAccuracy,
                             Long memberId, Attendance attendance) {
        this.attendanceType = attendanceType;
        this.attendanceStatus = attendanceStatus;
        this.locationAccuracy = locationAccuracy;
        this.memberId = memberId;
        this.attendance = attendance;
    }

    public static AttendanceRecord onLineRecord(Attendance attendance, Long memberId,
                                                AttendanceStatus attendanceStatus) {
        return new AttendanceRecord(
                AttendanceType.ONLINE,
                attendanceStatus,
                null,
                memberId,
                attendance
        );
    }

    public static AttendanceRecord offlineRecord(Attendance attendance, Long memberId, Double locationAccuracy,
                                                 AttendanceStatus attendanceStatus) {
        return new AttendanceRecord(
                AttendanceType.OFFLINE,
                attendanceStatus,
                locationAccuracy,
                memberId,
                attendance
        );
    }
}
