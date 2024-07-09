package org.cotato.csquiz.domain.education.entity;

import org.cotato.csquiz.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "quiz_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scorer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scorer_id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    @Column(name = "ticket_number", nullable = false)
    private Long ticketNumber;

    private Scorer(final Long memberId, Quiz quiz, Long ticketNumber) {
        this.memberId = memberId;
        this.quizId = quiz.getId();
        this.ticketNumber = ticketNumber;
    }

    public static Scorer of(final Long memberId, Quiz quiz, Long ticketNumber) {
        return new Scorer(memberId, quiz, ticketNumber);
    }

    public void updateMemberId(final Long memberId) {
        this.memberId = memberId;
    }

    public void updateScorer(final Long memberId, Long ticketNumber){
        this.memberId = memberId;
        this.ticketNumber = ticketNumber;
    }
}
