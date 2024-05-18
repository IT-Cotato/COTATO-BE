package cotato.csquiz.domain.entity;

import static jakarta.persistence.FetchType.LAZY;

import cotato.csquiz.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(uniqueConstraints = @UniqueConstraint(
        columnNames = {"member_id", "education_id"}
))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KingMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "king_member_id")
    private Long id;

    @Column(name = "member_id", nullable = false, updatable = false)
    private Long memberId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "education_id")
    private Education education;

    private KingMember(Member member, Education education) {
        this.memberId = member.getId();
        this.education = education;
    }

    public static KingMember of(Member member, Education education) {
        return new KingMember(
                member,
                education
        );
    }
}
