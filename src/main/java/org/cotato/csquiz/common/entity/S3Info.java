package org.cotato.csquiz.common.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class S3Info {
    private String folderName;
    private String fileName;
    private String uploadUrl;

    @Builder
    public S3Info(String s3Url, String fileName, String folderName) {
        this.uploadUrl = s3Url;
        this.fileName = fileName;
        this.folderName = folderName;
    }
}
