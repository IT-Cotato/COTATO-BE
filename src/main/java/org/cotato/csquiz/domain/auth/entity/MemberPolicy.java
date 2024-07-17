package org.cotato.csquiz.domain.auth.entity;

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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberPolicy extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_policy_id")
    private Long id;

    @Column(name = "is_checked", nullable = false)
    private Boolean isChecked;

    @Column(name = "check_time", nullable = false)
    private LocalDateTime checkTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    private Policy policy;

    private MemberPolicy(Boolean isChecked, Member member, Policy policy) {
        this.isChecked = isChecked;
        this.member = member;
        this.policy = policy;
        this.checkTime = LocalDateTime.now();
    }

    public static MemberPolicy of(Boolean isChecked, Member member, Policy policy){
        return new MemberPolicy(isChecked, member, policy);
    }
}
