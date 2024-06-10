package org.cotato.csquiz.domain.generation.entity;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.common.entity.S3Info;
import org.cotato.csquiz.domain.generation.enums.CSEducation;
import org.cotato.csquiz.domain.generation.enums.ItIssue;
import org.cotato.csquiz.domain.generation.enums.Networking;
import org.cotato.csquiz.common.entity.BaseTimeEntity;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Session extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long id;

    @Column(name = "session_number")
    private Integer number;

    @Embedded
    private S3Info photoS3Info;

    @Column(name = "session_description")
    private String description;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "generation_id")
    private Generation generation;

    @Column(name = "session_it_issue")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'IT_OFF'")
    private ItIssue itIssue;

    @Column(name = "session_networking")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'NW_OFF'")
    private Networking networking;

    @Column(name = "session_cs_education")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'CS_OFF'")
    private CSEducation csEducation;

    @Builder
    public Session(int number, S3Info s3Info, String description, Generation generation, ItIssue itIssue,
                   CSEducation csEducation, Networking networking) {
        this.number = number;
        this.photoS3Info = s3Info;
        this.description = description;
        this.generation = generation;
        this.itIssue = itIssue;
        this.csEducation = csEducation;
        this.networking = networking;
    }

    public void changeSessionNumber(int sessionNumber) {
        this.number = sessionNumber;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void changePhotoUrl(S3Info photoUrl) {
        this.photoS3Info = photoUrl;
    }

    public void updateToggle(ItIssue itIssue, CSEducation csEducation, Networking networking) {
        this.itIssue = itIssue;
        this.csEducation = csEducation;
        this.networking = networking;
    }
}
