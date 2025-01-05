package org.cotato.csquiz.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.common.entity.S3Info;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.common.entity.BaseTimeEntity;
import org.cotato.csquiz.domain.auth.enums.MemberStatus;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Email
    @Column(name = "member_email")
    private String email;

    @Column(name = "member_password")
    private String password;

    @Column(name = "member_phone")
    private String phoneNumber;

    @Column(name = "member_name")
    private String name;

    @Column(name = "member_position", nullable = false)
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'NONE'")
    private MemberPosition position;

    @Column(name = "member_role")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'GENERAL'")
    private MemberRole role;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'REQUESTED")
    private MemberStatus status;

    @Column(name = "passed_generation_number")
    private Integer passedGenerationNumber;

    @Column(name = "member_profile_image")
    private S3Info profileImage;

    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;

    @Column(name = "university")
    private String university;

    private Member(String email, String password, String name, String phoneNumber, MemberStatus status) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    public static Member defaultMember(String email, String password, String name, String phoneNumber) {
        return new Member(email, password, name, phoneNumber, MemberStatus.REQUESTED);
    }

    public void updateRole(MemberRole role) {
        this.role = role;
    }

    public void updateStatus(MemberStatus memberStatus) {
        this.status = memberStatus;
    }

    public void approveMember() {
        this.status = MemberStatus.APPROVED;
        this.role = MemberRole.MEMBER;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateGeneration(Integer passedGenerationNumber) {
        this.passedGenerationNumber = passedGenerationNumber;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updatePosition(MemberPosition position) {
        this.position = position;
    }

    public void updateProfileImage(S3Info s3Info) {
        this.profileImage = s3Info;
    }

    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void updateUniversity(String university) {
        this.university = university;
    }
}
