package org.cotato.csquiz.api.attendance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.domain.attendance.embedded.Location;
import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;

@Getter
@NoArgsConstructor
public class OfflineAttendanceRequest implements AttendanceParams {

    @Schema(description = "출석 PK")
    @NotNull(message = "출석 PK를 입력해주세요")
    private Long attendanceId;

    @Schema(description = "회원 요청 시간")
    @NotNull(message = "출결 입력 시간을 적어주세요.")
    private LocalDateTime requestTime;

    @Schema(description = "사용자 요청 위치")
    @NotNull(message = "위치를 입력해주세요.")
    private Location location;

    @Override
    public AttendanceType attendanceType() {
        return AttendanceType.OFFLINE;
    }

    @Override
    public AttendanceResult attendanceResult() {
        return AttendanceResult.OFFLINE;
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
