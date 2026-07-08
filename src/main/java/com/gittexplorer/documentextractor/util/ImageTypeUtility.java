package com.gittexplorer.documentextractor.util;

import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ImageTypeUtility {

    private static final Set<String> RASTER_IMAGE_EXTENSIONS = Set.of(
            "jpg", "jpeg", "tiff", "tif", "bmp", "png", "gif", "avif");

    public boolean isRasterImage(String fileName, String mimeType) {
        String normalizedMimeType = mimeType == null ? "" : mimeType.toLowerCase(Locale.ROOT);
        if (normalizedMimeType.startsWith("image/") && !"image/svg+xml".equals(normalizedMimeType)) {
            return true;
        }

        int extensionStart = fileName == null ? -1 : fileName.lastIndexOf('.');
        if (extensionStart < 0 || extensionStart == fileName.length() - 1) {
            return false;
        }

        String extension = fileName.substring(extensionStart + 1).toLowerCase(Locale.ROOT);
        return RASTER_IMAGE_EXTENSIONS.contains(extension);
    }
}
