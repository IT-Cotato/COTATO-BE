package cotato.csquiz.controller.dto.quiz;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMultipleQuizRequest {

    private Integer number;
    private String question;
    private MultipartFile image;
    private List<CreateChoiceRequest> choices;
}
