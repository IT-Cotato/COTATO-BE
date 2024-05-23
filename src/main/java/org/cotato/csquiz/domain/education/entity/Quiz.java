package org.cotato.csquiz.domain.education.entity;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Embedded;
import org.cotato.csquiz.common.entity.BaseTimeEntity;
import org.cotato.csquiz.common.entity.S3Info;
import org.cotato.csquiz.domain.education.enums.QuizStatus;
import org.cotato.csquiz.domain.education.enums.QuizType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;


@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quiz extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Long id;

    @Column(name = "quiz_number", nullable = false)
    private Integer number;

    @Column(name = "quiz_question", nullable = false, length = 500)
    private String question;

    @Column(name = "quiz_photo_url")
    private String photoUrl;

    @Embedded
    private S3Info s3Info;

    @Column(name = "quiz_status")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'QUIZ_OFF'")
    private QuizStatus status;

    @Column(name = "quiz_start")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'QUIZ_OFF'")
    private QuizStatus start;

    @Column(name = "quiz_appear_second")
    private int appearSecond;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "education_id")
    private Education education;

    @OneToMany(mappedBy = "quiz", orphanRemoval = true)
    private List<Record> records = new ArrayList<>();

    public Quiz(Integer number, String question, S3Info s3Info, Education education, int appearSecond) {
        this.number = number;
        this.question = question;
        this.s3Info = s3Info;
        this.education = education;
        this.appearSecond = appearSecond;
    }

    public List<Record> getRecords() {
        return new ArrayList<>(this.records);
    }

    public void updateStatus(QuizStatus status) {
        this.status = status;
    }

    public void updateStart(QuizStatus status) {
        this.start = status;
    }

    public boolean isOff() {
        return status == QuizStatus.QUIZ_OFF;
    }

    public boolean isStart() {
        return start == QuizStatus.QUIZ_ON;
    }

    public QuizType getQuizType() {
        if (this instanceof MultipleQuiz) {
            return QuizType.MULTIPLE_QUIZ;
        }
        return QuizType.SHORT_QUIZ;
    }
}
