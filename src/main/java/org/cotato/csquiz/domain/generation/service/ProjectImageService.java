package org.cotato.csquiz.domain.generation.service;

import static org.cotato.csquiz.common.util.FileUtil.convert;
import static org.cotato.csquiz.common.util.FileUtil.convertToWebp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.common.entity.S3Info;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.cotato.csquiz.common.s3.S3Uploader;
import org.cotato.csquiz.domain.generation.entity.ProjectImage;
import org.cotato.csquiz.domain.generation.enums.ProjectImageType;
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
    public void createProjectImage(Long projectId, MultipartFile logoImage, MultipartFile thumbNailImage, List<MultipartFile> detailImages)
            throws ImageException {
        List<ProjectImage> newImages = new ArrayList<>();

        addImage(newImages, logoImage, ProjectImageType.LOGO, projectId, 1);
        addImage(newImages, thumbNailImage, ProjectImageType.THUMBNAIL, projectId, 1);

        if (detailImages != null && !detailImages.isEmpty()) {
            for (int orderIndex = 1; orderIndex <= detailImages.size(); orderIndex++) {
                MultipartFile detailImage = detailImages.get(orderIndex - 1);
                addImage(newImages, detailImage, ProjectImageType.DETAIL, projectId, orderIndex);
            }
        }

        projectImageRepository.saveAll(newImages);
    }

    private void addImage( List<ProjectImage> newImages, MultipartFile image, ProjectImageType imageType, Long projectId, int imageOrder) throws ImageException {
        File webpImage = convertToWebp(convert(image));
        S3Info ImageInfo = s3Uploader.uploadFiles(webpImage, PROJECT_IMAGE);
        newImages.add(ProjectImage.createProjectImage(imageType, ImageInfo, projectId, imageOrder));
    }
}
