package org.cotato.csquiz.domain.generation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "position")
    private MemberPosition memberPosition;

    @Column(name = "name")
    private String name;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Builder
    public ProjectMember(MemberPosition memberPosition, String name, Long projectId) {
        this.memberPosition = memberPosition;
        this.name = name;
        this.projectId = projectId;
    }

    public static ProjectMember of(MemberPosition memberPosition, String name, Project project) {
        return new ProjectMember(memberPosition, name, project.getId());
    }
}
