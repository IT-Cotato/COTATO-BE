package org.cotato.csquiz.api.quiz.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMultipleQuizRequest {

	private Integer number;
	private String question;
	private MultipartFile image;
	private List<CreateChoiceRequest> choices;
}
