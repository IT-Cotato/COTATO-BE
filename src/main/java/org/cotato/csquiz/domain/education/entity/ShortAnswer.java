package org.cotato.csquiz.domain.education.entity;

import static jakarta.persistence.FetchType.LAZY;

import org.cotato.csquiz.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ShortAnswer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "short_answer_id")
    private Long id;

    @Column(name = "short_answer_content")
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "quiz_id")
    private ShortQuiz shortQuiz;

    private ShortAnswer(String content, ShortQuiz shortQuiz) {
        this.content = content;
        this.shortQuiz = shortQuiz;
    }

    public static ShortAnswer of(String content, ShortQuiz shortQuiz) {
        return new ShortAnswer(
                content,
                shortQuiz
        );
    }
}
