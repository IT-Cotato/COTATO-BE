package org.cotato.csquiz.api.session.dto;

import java.util.List;

public record UpdateSessionPhotoOrderRequest(
        Long sessionId,
        List<UpdateSessionPhotoOrderInfoRequest> orderInfos
) {
}
