package org.cotato.csquiz.domain.recruitment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.common.entity.BaseTimeEntity;
import org.cotato.csquiz.domain.auth.entity.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentNotification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(name = "generation_number", nullable = false)
    private int generationNumber;

    @Column(name = "send_time", nullable = false)
    private LocalDateTime sendTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender")
    private Member sender;

    private RecruitmentNotification(int generationNumber, LocalDateTime sendTime, Member sender) {
        this.generationNumber = generationNumber;
        this.sendTime = sendTime;
        this.sender = sender;
    }

    public static RecruitmentNotification of(Member member, int generationNumber) {
        return new RecruitmentNotification(
                generationNumber,
                LocalDateTime.now(),
                member
        );
    }
}
