package cotato.csquiz.controller.dto.member;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record UpdateActiveMemberToOldMemberRequest(
        @NotNull
        List<Long> memberIds
) {
}
