package org.cotato.csquiz.api.attendance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.domain.attendance.embedded.Location;
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;

@Getter
@NoArgsConstructor
public class OfflineAttendanceRequest implements AttendanceParams {

    @Schema(description = "출석 PK")
    @NotNull
    private Long attendanceId;

    @Schema(description = "회원 요청 시간")
    private LocalDateTime requestTime;

    @Schema(description = "사용자 요청 위치")
    @NotNull
    private Location location;

    @Override
    public AttendanceType attendanceType() {
        return AttendanceType.OFFLINE;
    }

    @Override
    public Long attendanceId() {
        return attendanceId;
    }

    @Override
    public LocalDateTime requestTime() {
        return requestTime;
    }
}
