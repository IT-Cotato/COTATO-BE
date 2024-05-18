package cotato.csquiz.domain.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(value = "MultipleQuiz")
public class MultipleQuiz extends Quiz {

    @Builder
    public MultipleQuiz(Integer number, String question, String photoUrl, Education education, int appearSecond) {
        super(number, question, photoUrl, education, appearSecond);
    }
}
