package cotato.csquiz.controller.dto.generation;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AddGenerationRequest(
        @NotNull
        Integer generationNumber,
        @NotNull
        LocalDate startDate,
        @NotNull
        LocalDate endDate,
        @NotNull
        Integer sessionCount
) {
}
