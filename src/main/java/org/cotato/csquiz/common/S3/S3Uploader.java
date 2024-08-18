package org.cotato.csquiz.common.S3;

import static org.cotato.csquiz.common.util.FileUtil.extractFileExtension;
import static org.cotato.csquiz.common.util.FileUtil.isImageFileExtension;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.cotato.csquiz.common.entity.S3Info;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.ImageException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private static final String CONTENT_TYPE = "multipart/formed-data";
    private final AmazonS3Client amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public S3Info uploadFiles(MultipartFile multipartFile, String folderName) throws ImageException {
        log.info("{} 사진 업로드", multipartFile.getOriginalFilename());
        File localUploadFile = convert(multipartFile);

        String fileName = folderName + "/" + localUploadFile.getName();
        String uploadUrl = putS3(localUploadFile, fileName);
        localUploadFile.delete();

        return S3Info.builder()
                .folderName(folderName)
                .fileName(localUploadFile.getName())
                .url(uploadUrl)
                .build();
    }

    public void deleteFile(S3Info s3Info) {
        String fileName = s3Info.getFolderName() + "/" + s3Info.getFileName();

        log.info("{} 사진 삭제", fileName);
        amazonS3.deleteObject(bucket, fileName);
    }

    private String putS3(File uploadFile, String fileName) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead);

        if (isImageFile(uploadFile)) {
            ObjectMetadata objMeta = new ObjectMetadata();
            objMeta.setContentType(CONTENT_TYPE);
        }

        amazonS3.putObject(putObjectRequest);

        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private boolean isImageFile(File file) {
        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

        return isImageFileExtension(extension);
    }

    private File convert(MultipartFile file) throws ImageException {
        String fileExtension = extractFileExtension(file);
        File convertFile = new File(System.getProperty("user.dir") + "/" + UUID.randomUUID() + "." + fileExtension);

        try {
            FileOutputStream fos = new FileOutputStream(convertFile);
            fos.write(file.getBytes());
            fos.close();

            return convertFile;
        } catch (IOException e) {
            throw new ImageException(ErrorCode.IMAGE_CONVERT_FAIL);
        }
    }
}
