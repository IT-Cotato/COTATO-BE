package cotato.csquiz.controller.dto.generation;

import cotato.csquiz.domain.entity.Generation;

public record AddGenerationResponse(
        Long generationId
) {
    public static AddGenerationResponse from(Generation generation) {
        return new AddGenerationResponse(generation.getId());
    }
}
