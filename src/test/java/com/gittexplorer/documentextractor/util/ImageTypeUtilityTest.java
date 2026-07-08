package com.gittexplorer.documentextractor.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ImageTypeUtilityTest {

    private final ImageTypeUtility utility = new ImageTypeUtility();

    @Test
    void detectsRasterImagesButExcludesSvg() {
        assertThat(utility.isRasterImage("scan.png", "image/png")).isTrue();
        assertThat(utility.isRasterImage("scan.tiff", null)).isTrue();
        assertThat(utility.isRasterImage("diagram.svg", "image/svg+xml")).isFalse();
        assertThat(utility.isRasterImage("document.pdf", "application/pdf")).isFalse();
    }
}
