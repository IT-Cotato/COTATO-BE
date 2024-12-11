package org.cotato.csquiz.api.session.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.cotato.csquiz.api.attendance.dto.AttendanceTimeResponse;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.generation.embedded.SessionContents;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.entity.SessionImage;

public record SessionWithAttendanceResponse(
        Long sessionId,
        Integer sessionNumber,
        String title,
        List<SessionListImageInfoResponse> sessionImages,
        String description,
        Long generationId,
        String placeName,
        LocalDateTime sessionDateTime,
        SessionContents sessionContents,
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
            session.getSessionDateTime(),
            session.getSessionContents(),
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
                session.getSessionDateTime(),
                session.getSessionContents(),
                null
        );
    }
}
