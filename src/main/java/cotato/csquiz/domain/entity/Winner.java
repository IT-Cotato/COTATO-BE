package cotato.csquiz.domain.entity;

import static jakarta.persistence.FetchType.LAZY;

import cotato.csquiz.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Winner extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "winner_id")
    private Long id;

    @Column(name = "member_id", nullable = false, updatable = false)
    private Long memberId;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "education_id")
    private Education education;

    private Winner(Long memberId, Education education) {
        this.memberId = memberId;
        this.education = education;
    }

    public static Winner of(Long memberId, Education education) {
        return new Winner(
                memberId,
                education
        );
    }
}
