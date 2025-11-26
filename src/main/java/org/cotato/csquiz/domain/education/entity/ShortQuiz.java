package org.cotato.csquiz.domain.education.entity;

import org.cotato.csquiz.common.entity.S3Info;
import org.hibernate.annotations.DynamicInsert;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(value = "ShortQuiz")
public class ShortQuiz extends Quiz {

	@Builder
	public ShortQuiz(Integer number, String question, S3Info s3Info, Education education, int appearSecond) {
		super(number, question, s3Info, education, appearSecond);
	}
}
