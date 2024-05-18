package org.cotato.csquiz.domain.education.entity;

import static jakarta.persistence.FetchType.LAZY;

import org.cotato.csquiz.api.quiz.dto.CreateChoiceRequest;
import org.cotato.csquiz.domain.education.enums.ChoiceCorrect;
import org.cotato.csquiz.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Choice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "choice_id")
    private Long id;

    @Column(name = "choice_number")
    private Integer choiceNumber;

    @Column(name = "choice_content")
    private String content;

    @Column(name = "choice_correct")
    @Enumerated(EnumType.STRING)
    private ChoiceCorrect isCorrect;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "quiz_id")
    private MultipleQuiz multipleQuiz;

    private Choice(Integer choiceNumber, String content, ChoiceCorrect isCorrect, MultipleQuiz multipleQuiz) {
        this.choiceNumber = choiceNumber;
        this.content = content;
        this.isCorrect = isCorrect;
        this.multipleQuiz = multipleQuiz;
    }

    public static Choice of(CreateChoiceRequest request, MultipleQuiz multipleQuiz) {
        return new Choice(
                request.getNumber(),
                request.getContent(),
                request.getIsAnswer(),
                multipleQuiz
        );
    }

    public void updateCorrect(ChoiceCorrect choiceCorrect) {
        this.isCorrect = choiceCorrect;
    }
}
