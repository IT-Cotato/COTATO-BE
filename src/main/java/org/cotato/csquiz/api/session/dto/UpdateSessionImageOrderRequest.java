package org.cotato.csquiz.api.session.dto;

import java.util.List;

public record UpdateSessionImageOrderRequest(
        Long sessionId,
        List<UpdateSessionImageOrderInfoRequest> orderInfos
) {
}
