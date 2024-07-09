package org.cotato.csquiz.api.session.dto;

import jakarta.validation.constraints.NotNull;

public record DeleteSessionPhotoRequest(
        @NotNull
        Long photoId
) {
}
