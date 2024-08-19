package org.cotato.csquiz.common.util;

import java.util.List;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.springframework.web.multipart.MultipartFile;

public class FileUtil {

    private static final List<String> IMAGE_FILE_EXTENSIONS = List.of("png", "jpg", "jpeg", "heif");

    public static String extractFileExtension(MultipartFile file) throws ImageException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new ImageException(ErrorCode.FILE_EXTENSION_FAULT);
        }

        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
    }

    public static boolean isImageFileExtension(String fileExtension) {
        return IMAGE_FILE_EXTENSIONS.contains(fileExtension.toLowerCase());
    }
}
