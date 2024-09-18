package org.cotato.csquiz.domain.generation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.common.entity.S3Info;
import org.cotato.csquiz.domain.generation.enums.ProjectImageType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_image_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_image_type")
    private ProjectImageType projectImageType;

    @Embedded
    private S3Info s3Info;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "project_image_order", nullable = false)
    private int imageOrder;

    private ProjectImage(ProjectImageType projectImageType, S3Info s3Info, Long projectId, int imageOrder) {
        this.projectImageType = projectImageType;
        this.s3Info = s3Info;
        this.projectId = projectId;
        this.imageOrder = imageOrder;
    }

    public static ProjectImage logoImage(S3Info s3Info, Long projectId) {
        return new ProjectImage(
                ProjectImageType.LOGO,
                s3Info,
                projectId,
                1);
    }

    public static ProjectImage thumbnailImage(S3Info s3Info, Long projectId) {
        return new ProjectImage(
                ProjectImageType.THUMBNAIL,
                s3Info,
                projectId,
                1);
    }

    public static ProjectImage detailImage(S3Info imageInfo, Long projectId, int imageOrder) {
        return new ProjectImage(
                ProjectImageType.DETAIL,
                imageInfo,
                projectId,
                imageOrder
        );
    }
}
