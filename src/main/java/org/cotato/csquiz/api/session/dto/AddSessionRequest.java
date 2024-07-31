package org.cotato.csquiz.api.session.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.cotato.csquiz.domain.attendance.embedded.Location;
import org.cotato.csquiz.domain.generation.enums.CSEducation;
import org.cotato.csquiz.domain.generation.enums.DevTalk;
import org.cotato.csquiz.domain.generation.enums.ItIssue;
import org.cotato.csquiz.domain.generation.enums.Networking;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record AddSessionRequest(
        @NotNull
        Long generationId,
        List<MultipartFile> images,
        @NotNull
        String title,
        @NotNull
        String description,
        Location location,
        String placeName,
        @NotNull
        LocalDate sessionDate,
        @NotNull
        AttendanceDeadLine attendanceDeadLine,
        ItIssue itIssue,
        Networking networking,
        CSEducation csEducation,
        DevTalk devTalk
) {
        public record AttendanceDeadLine(
                @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
                LocalTime startTime,
                @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
                LocalTime endTime
        ){
        }
}
