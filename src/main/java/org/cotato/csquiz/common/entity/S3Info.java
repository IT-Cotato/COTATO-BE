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
    private String url;

    @Builder
    public S3Info(String url, String fileName, String folderName) {
        this.url = url;
        this.fileName = fileName;
        this.folderName = folderName;
    }
}
