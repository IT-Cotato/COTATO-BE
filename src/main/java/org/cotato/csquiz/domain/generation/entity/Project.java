package org.cotato.csquiz.domain.generation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    @Column(name = "project_name")
    private String name;

    @Column(name = "project_introduction")
    private String introduction;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "behance_url")
    private String behanceUrl;

    @Column(name = "project_url")
    private String projectUrl;

    @Column(name = "generation_id", nullable = false)
    private Long generationId;

    @Builder
    public Project(String name, String introduction, String githubUrl, String behanceUrl, Long generationId) {
        this.name = name;
        this.introduction = introduction;
        this.githubUrl = githubUrl;
        this.behanceUrl = behanceUrl;
        this.generationId = generationId;
    }
}