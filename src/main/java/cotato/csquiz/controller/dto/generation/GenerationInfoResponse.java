package cotato.csquiz.controller.dto.generation;

import cotato.csquiz.domain.entity.Generation;

public record GenerationInfoResponse(
        Long generationId,
        Integer generationNumber,
        Integer sessionCount
) {

    public static GenerationInfoResponse from(Generation generation) {
        return new GenerationInfoResponse(
                generation.getId(),
                generation.getNumber(),
                generation.getSessionCount()
        );
    }
}
