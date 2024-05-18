package cotato.csquiz.controller.dto.generation;


import jakarta.validation.constraints.NotNull;

public record ChangeRecruitingStatusRequest(
        @NotNull
        Long generationId,
        @NotNull
        boolean statement
) {
}
