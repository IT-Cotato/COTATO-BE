package org.cotato.csquiz.domain.education.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.common.entity.S3Info;
import org.cotato.csquiz.domain.education.entity.Education;
import org.cotato.csquiz.domain.education.entity.Quiz;
import org.hibernate.annotations.DynamicInsert;

@Entity
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(value = "MultipleQuiz")
public class MultipleQuiz extends Quiz {

    @Builder
    public MultipleQuiz(Integer number, String question, S3Info s3Info, Education education, int appearSecond) {
        super(number, question, s3Info, education, appearSecond);
    }
}
