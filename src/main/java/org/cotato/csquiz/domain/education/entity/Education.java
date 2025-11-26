package org.cotato.csquiz.domain.education.entity;

import org.cotato.csquiz.common.entity.BaseTimeEntity;
import org.cotato.csquiz.domain.education.enums.EducationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Education extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "education_id")
	private Long id;

	@Column(name = "education_number")
	private Integer number;

	@Column(name = "education_subject")
	private String subject;

	@Column(name = "education_status")
	@Enumerated(EnumType.STRING)
	private EducationStatus status;

	@Column(name = "generation_id", nullable = false)
	private Long generationId;

	@Builder
	public Education(String subject, Integer educationNumber, Long generationId) {
		this.subject = subject;
		this.number = educationNumber;
		this.generationId = generationId;
		status = EducationStatus.BEFORE;
	}

	public void updateStatus(EducationStatus status) {
		this.status = status;
	}

	public void updateSubject(String newSubject) {
		this.subject = newSubject;
	}

	public void updateNumber(Integer changeNumber) {
		this.number = changeNumber;
	}
}
