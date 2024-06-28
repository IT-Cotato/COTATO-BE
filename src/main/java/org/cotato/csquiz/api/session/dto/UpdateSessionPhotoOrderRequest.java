package org.cotato.csquiz.api.session.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateSessionPhotoOrderRequest(
        @NotNull
        Long photoId,
        @NotNull
        Integer order
) {
}
