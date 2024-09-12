package org.cotato.csquiz.api.session.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.cotato.csquiz.domain.generation.enums.CSEducation;
import org.cotato.csquiz.domain.generation.enums.DevTalk;
import org.cotato.csquiz.domain.generation.enums.ItIssue;
import org.cotato.csquiz.domain.generation.enums.Networking;
import org.springframework.web.multipart.MultipartFile;

public record AddSessionRequest(
        @NotNull
        Long generationId,
        List<MultipartFile> images,
        @NotNull
        String title,
        @NotNull
        String description,
        Double latitude,
        Double longitude,
        String placeName,
        @Schema(description = "세션 날짜 및 시작 시간")
        @NotNull
        LocalDateTime sessionDateTime,

        @Schema(example = "19:10:00", description = "출석 마감 시간, 해당 시간 이후 지각  처리")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        LocalTime attendanceDeadLine,

        @Schema(example = "19:20:00", description = "지각 마감 시간, 해당 시간 이후 결석 처리")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        LocalTime lateDeadLine,

        ItIssue itIssue,
        Networking networking,
        CSEducation csEducation,
        DevTalk devTalk
) {
}
