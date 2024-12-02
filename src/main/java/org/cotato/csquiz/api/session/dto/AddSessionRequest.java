package org.cotato.csquiz.api.session.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
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

        @Schema(description = "대면 출결 진행 여부")
        boolean isOffline,

        @Schema(description = "비대면 세션 진행 여부")
        boolean isOnline,

        @Schema(example = "2024-11-11T19:10:00", description = "출석 마감 시간, 해당 시간 이후 지각  처리")
        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @NotNull
        LocalDateTime attendanceDeadLine,

        @Schema(example = "2024-11-11T19:20:00", description = "지각 마감 시간, 해당 시간 이후 결석 처리")
        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @NotNull
        LocalDateTime lateDeadLine,

        ItIssue itIssue,
        Networking networking,
        CSEducation csEducation,
        DevTalk devTalk
) {
}
