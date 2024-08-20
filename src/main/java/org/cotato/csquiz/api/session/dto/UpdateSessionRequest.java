package org.cotato.csquiz.api.session.dto;

import java.time.LocalDate;
import org.cotato.csquiz.api.attendance.dto.AttendanceDeadLineDto;
import org.cotato.csquiz.domain.attendance.embedded.Location;
import org.cotato.csquiz.domain.generation.enums.CSEducation;
import org.cotato.csquiz.domain.generation.enums.DevTalk;
import org.cotato.csquiz.domain.generation.enums.ItIssue;
import org.cotato.csquiz.domain.generation.enums.Networking;
import jakarta.validation.constraints.NotNull;

public record UpdateSessionRequest(
        @NotNull
        Long sessionId,
        String title,
        String description,
        @NotNull
        LocalDate sessionDate,
        String placeName,
        Location location,
        AttendanceDeadLineDto attendTime,
        @NotNull
        ItIssue itIssue,
        @NotNull
        Networking networking,
        @NotNull
        CSEducation csEducation,
        @NotNull
        DevTalk devTalk
) {
}
