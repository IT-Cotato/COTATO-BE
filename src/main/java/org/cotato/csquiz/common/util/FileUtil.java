package org.cotato.csquiz.common.util;

import java.util.Arrays;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.springframework.web.multipart.MultipartFile;

public class FileUtil {

    private static final String[] ALLOWED_IMAGE_FILE_EXTENSIONS = {"png", "jpg", "jpeg", "heif"};

    public static String extractFileExtension(MultipartFile file) throws ImageException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new ImageException(ErrorCode.FILE_EXTENSION_FAULT);
        }

        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
    }

    public static void checkAllowedImageFileExtension(String fileExtension) throws ImageException {
        if (!Arrays.asList(ALLOWED_IMAGE_FILE_EXTENSIONS).contains(fileExtension)) {
            throw new ImageException(ErrorCode.FILE_EXTENSION_FAULT);
        }
    }
}
