package com.gittexplorer.documentextractor.extractor;

import static org.assertj.core.api.Assertions.assertThat;

import com.gittexplorer.documentextractor.configuration.DocumentExtractionProperties;
import com.gittexplorer.documentextractor.util.FileNameUtility;
import com.gittexplorer.documentextractor.util.ImageTypeUtility;
import org.junit.jupiter.api.Test;

class LocalTesseractImageTextExtractorTest {

    @Test
    void respectsOcrEnabledFlag() {
        DocumentExtractionProperties properties = new DocumentExtractionProperties();
        properties.getOcr().setEnabled(false);

        LocalTesseractImageTextExtractor extractor = new LocalTesseractImageTextExtractor(
                properties,
                new FileNameUtility(),
                new ImageTypeUtility());

        assertThat(extractor.supports("scan.png", "image/png")).isFalse();
    }
}
