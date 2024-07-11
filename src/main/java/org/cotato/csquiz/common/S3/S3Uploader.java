package org.cotato.csquiz.common.S3;

import static org.cotato.csquiz.common.util.FileUtil.checkAllowedImageFileExtension;
import static org.cotato.csquiz.common.util.FileUtil.extractFileExtension;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
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
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final AmazonS3Client amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public S3Info uploadFiles(MultipartFile multipartFile, String folderName) throws ImageException {
        log.info("upload Files {}", multipartFile);
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new ImageException(ErrorCode.IMAGE_PROCESSING_FAIL));
        String uploadUrl = upload(uploadFile, folderName);

        return S3Info.builder()
                .folderName(folderName)
                .fileName(uploadFile.getName())
                .url(uploadUrl)
                .build();
    }

    public void deleteFile(S3Info s3Info) {
        String fileName = s3Info.getFolderName() + "/" + s3Info.getFileName();

        log.info("deleteFile fileName = {}", fileName);
        try {
            amazonS3.deleteObject(bucket, fileName);
        } catch (SdkClientException e) {
            log.error("Failed to delete file: {}", s3Info.getUrl(), e);
        }
    }

    private String upload(File uploadFile, String dirName) {
        String fileName = dirName + "/" + uploadFile.getName();
        String uploadUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        log.info(uploadUrl);
        return uploadUrl;
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("삭제 완료");
        } else {
            log.error("삭제 에러");
        }
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private Optional<File> convert(MultipartFile file) throws ImageException {
        String fileExtension = extractFileExtension(file);
        checkAllowedImageFileExtension(fileExtension);

        File convertFile = new File(System.getProperty("user.dir") + "/" + UUID.randomUUID() + "." + fileExtension);
        log.info("converted file name: {}", convertFile.getName());

        try {
            log.info("convert try start");
            if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
                FileOutputStream fos = new FileOutputStream(convertFile); // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                fos.write(file.getBytes());
                fos.close();
                log.info("convert to " + convertFile);
                return Optional.of(convertFile);
            }
        } catch (IOException e) {
            log.error("convert 실패", e);
            throw new ImageException(ErrorCode.IMAGE_PROCESSING_FAIL);
        }
        log.info("convert empty");
        return Optional.empty();
    }
}
