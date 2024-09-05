package org.cotato.csquiz.common.util;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import java.io.File;
import java.io.IOException;
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

    public static File convertToWebp(File originalFile) throws ImageException {
        try {
            String fileNameWithoutExtension = originalFile.getName().replaceFirst("[.][^.]+$", "");

            File webpFile = ImmutableImage.loader()
                    .fromFile(originalFile)
                    .output(WebpWriter.DEFAULT, new File(fileNameWithoutExtension + ".webp"));

            originalFile.delete();
            return webpFile;
        } catch (IOException e) {
            throw new ImageException(ErrorCode.WEBP_CONVERT_FAIL);
        }
    }
}
