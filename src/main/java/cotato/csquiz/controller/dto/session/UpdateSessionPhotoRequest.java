package cotato.csquiz.controller.dto.session;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

@Valid
public record UpdateSessionPhotoRequest(
        @NotNull
        Long sessionId,
        MultipartFile sessionImage
) {
}
