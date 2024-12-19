package org.cotato.csquiz.domain.education.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.common.entity.BaseTimeEntity;
import org.cotato.csquiz.common.entity.S3Info;
import org.cotato.csquiz.domain.education.embedded.Choices;
import org.cotato.csquiz.domain.education.enums.QuizCategory;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RandomQuiz extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private QuizCategory category;

    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    private String question;

    @Embedded
    private S3Info image;

    @Embedded
    private Choices choices;

    @Column(name = "answer_number", nullable = false)
    private Integer answerNumber;

    public List<String> getChoices() {
        if (Objects.isNull(choices)) {
            throw new IllegalStateException("선택지가 없는 경우가 존재합니다.");
        }
        return choices.getChoices();
    }

    public String getImageUrl() {
        if (Objects.isNull(image)) {
            return null;
        }
        return image.getUrl();
    }
}
