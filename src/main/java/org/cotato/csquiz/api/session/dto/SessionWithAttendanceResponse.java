package org.cotato.csquiz.api.session.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import org.cotato.csquiz.api.attendance.dto.AttendanceTimeResponse;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.generation.embedded.SessionContents;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.entity.SessionImage;

public record SessionWithAttendanceResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Long sessionId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Integer sessionNumber,
        String title,
        List<SessionListImageInfoResponse> sessionImages,
        String description,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Long generationId,
        String placeName,
        String roadNameAddress,
        LocalDateTime sessionDateTime,
        SessionContents sessionContents,
        boolean isOffline,
        boolean isOnline,
        AttendanceTimeResponse attendance
) {
    public static SessionWithAttendanceResponse of(Session session, List<SessionImage> sessionImages, Attendance attendance) {
        return new SessionWithAttendanceResponse(
            session.getId(),
            session.getNumber(),
            session.getTitle(),
            sessionImages.stream().map(SessionListImageInfoResponse::from).toList(),
            session.getDescription(),
            session.getGeneration().getId(),
            session.getPlaceName(),
            session.getRoadNameAddress(),
            session.getSessionDateTime(),
            session.getSessionContents(),
            session.getSessionType().hasOffline(),
            session.getSessionType().hasOnline(),
            AttendanceTimeResponse.from(attendance)
        );
    }

    public static SessionWithAttendanceResponse of(Session session, List<SessionImage> sessionImages) {
        return new SessionWithAttendanceResponse(
                session.getId(),
                session.getNumber(),
                session.getTitle(),
                sessionImages.stream().map(SessionListImageInfoResponse::from).toList(),
                session.getDescription(),
                session.getGeneration().getId(),
                session.getPlaceName(),
                session.getRoadNameAddress(),
                session.getSessionDateTime(),
                session.getSessionContents(),
                session.getSessionType().hasOffline(),
                session.getSessionType().hasOnline(),
                null
        );
    }
}
