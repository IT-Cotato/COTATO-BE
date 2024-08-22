package org.cotato.csquiz.domain.generation.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.project.dto.ProjectImageRequest;
import org.cotato.csquiz.common.entity.S3Info;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.cotato.csquiz.common.s3.S3Uploader;
import org.cotato.csquiz.domain.generation.entity.Project;
import org.cotato.csquiz.domain.generation.entity.ProjectImage;
import org.cotato.csquiz.domain.generation.repository.ProjectImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProjectImageService {

    private static final String PROJECT_IMAGE = "projects";
    private final S3Uploader s3Uploader;
    private final ProjectImageRepository projectImageRepository;

    @Transactional
    public void createProjectImage(Project project, MultipartFile logoImage, MultipartFile thumbNameImage)
            throws ImageException {
        List<ProjectImage> newImages = new ArrayList<>();

        S3Info logoImageInfo = s3Uploader.uploadFiles(logoImage, PROJECT_IMAGE);
        newImages.add(ProjectImage.logoImage(logoImageInfo, project));

        S3Info thumbNailInfo = s3Uploader.uploadFiles(thumbNameImage, PROJECT_IMAGE);
        newImages.add(ProjectImage.thumbNailImage(thumbNailInfo, project));

        projectImageRepository.saveAll(newImages);
    }
}
