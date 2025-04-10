package org.cotato.csquiz.domain.recruitment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.common.entity.BaseTimeEntity;
import org.cotato.csquiz.domain.recruitment.enums.SendStatus;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentNotificationRequester extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "requester_id")
    private Long id;

    @Column(name = "request_email", nullable = false)
    private String email;

    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime;

    @Column(name = "policy_checked", nullable = false)
    private Boolean policyChecked;

    @Column(name = "send_status", nullable = false)
    private SendStatus sendStatus;
}
