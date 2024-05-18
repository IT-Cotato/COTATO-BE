package org.cotato.csquiz.common.S3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
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

    public String uploadFiles(MultipartFile multipartFile, String dirName) throws ImageException {
        log.info("upload Files {}", multipartFile);
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new ImageException(ErrorCode.IMAGE_PROCESSING_FAIL));
        return upload(uploadFile, dirName, multipartFile.getOriginalFilename());
    }

    public void deleteFile(String fileUrl) throws ImageException {
        String[] splitFile = fileUrl.split("/");
        String fileName = splitFile[splitFile.length - 2] + "/" + splitFile[splitFile.length - 1];
        try {
            amazonS3.deleteObject(bucket, fileName);
        } catch (SdkClientException e) {
            throw new ImageException(ErrorCode.IMAGE_DELETE_FAIL);
        }
    }

    private String upload(File uploadFile, String dirName, String originalName) {
        String fileName = dirName + "/" + UUID.randomUUID() + originalName;
        String uploadUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        log.info(uploadUrl);
        return uploadUrl;
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("삭제 완료");
        } else {
            log.info("삭제 에러");
        }
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private Optional<File> convert(MultipartFile file) throws ImageException {
        File convertFile = new File(System.getProperty("user.dir") + "/" + UUID.randomUUID());
        log.info("original file name: {}", convertFile.getName());

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
