package org.cotato.csquiz.api.admin.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record UpdateActiveMemberToOldMemberRequest(
        @NotNull
        List<Long> memberIds
) {
}
