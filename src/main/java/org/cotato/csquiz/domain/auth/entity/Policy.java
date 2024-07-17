package org.cotato.csquiz.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.common.entity.BaseTimeEntity;
import org.cotato.csquiz.domain.auth.enums.PolicyType;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class Policy extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Long id;

    @Column(name = "policy_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'ESSENTIAL'")
    private PolicyType policyType;

    @Column(name = "policy_title", nullable = false)
    private String title;

    @Column(name = "policy_content", nullable = false, columnDefinition = "TEXT")
    private String content;
}
