package org.cotato.csquiz.domain.education.embedded;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Choices {

	@Column(nullable = false)
	private String firstChoice;

	@Column(nullable = false)
	private String secondChoice;

	@Column(nullable = false)
	private String thirdChoice;

	@Column(nullable = false)
	private String fourthChoice;

	public List<String> getChoices() {
		return List.of(firstChoice, secondChoice, thirdChoice, fourthChoice);
	}
}
