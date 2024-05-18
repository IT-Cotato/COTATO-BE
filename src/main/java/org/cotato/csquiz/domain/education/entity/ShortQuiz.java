package org.cotato.csquiz.domain.education.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.domain.education.entity.Education;
import org.cotato.csquiz.domain.education.entity.Quiz;
import org.hibernate.annotations.DynamicInsert;

@Entity
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(value = "ShortQuiz")
public class ShortQuiz extends Quiz {

    @Builder
    public ShortQuiz(Integer number, String question, String photoUrl, Education education, int appearSecond) {
        super(number, question, photoUrl, education, appearSecond);
    }
}
