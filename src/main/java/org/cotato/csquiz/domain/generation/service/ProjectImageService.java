package org.cotato.csquiz.domain.generation.service;

import static org.cotato.csquiz.common.util.FileUtil.convert;
import static org.cotato.csquiz.common.util.FileUtil.convertToWebp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.common.entity.S3Info;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.cotato.csquiz.common.s3.S3Uploader;
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
    public void createProjectImage(Long projectId, MultipartFile logoImage, MultipartFile thumbNailImage,
                                   List<MultipartFile> detailImages) throws ImageException {

        Optional<ProjectImage> existingLogoImage = projectImageRepository.findLogoImageByProjectId(projectId);
        if (existingLogoImage.isPresent()) {
            throw new AppException(ErrorCode.LOGO_IMAGE_EXIST);
        }

        Optional<ProjectImage> existingThumbnailImage = projectImageRepository.findThumbnailImageByProjectId(projectId);
        if (existingThumbnailImage.isPresent()) {
            throw new AppException(ErrorCode.THUMBNAIL_IMAGE_EXIST);
        }

        List<ProjectImage> newImages = new ArrayList<>();

        File webpLogoImage = convertToWebp(convert(logoImage));
        S3Info logoImageInfo = s3Uploader.uploadFiles(webpLogoImage, PROJECT_IMAGE);
        newImages.add(ProjectImage.logoImage(logoImageInfo, projectId));

        File webpThumbNailImage = convertToWebp(convert(thumbNailImage));
        S3Info thumbNailInfo = s3Uploader.uploadFiles(webpThumbNailImage, PROJECT_IMAGE);
        newImages.add(ProjectImage.thumbnailImage(thumbNailInfo, projectId));

        if (detailImages != null && !detailImages.isEmpty()) {
            for (int orderIndex = 0; orderIndex < detailImages.size(); orderIndex++) {
                MultipartFile detailImage = detailImages.get(orderIndex);
                File webpDetailImage = convertToWebp(convert(detailImage));
                S3Info detailImageInfo = s3Uploader.uploadFiles(webpDetailImage, PROJECT_IMAGE);
                newImages.add(ProjectImage.detailImage(detailImageInfo, projectId, orderIndex));
            }
        }

        projectImageRepository.saveAll(newImages);
    }
}
