package org.cotato.csquiz.api.quiz.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateShortQuizRequest {

	private int number;
	private String question;
	private MultipartFile image;
	private List<CreateShortAnswerRequest> shortAnswers;
}
