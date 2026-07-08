package com.gittexplorer.documentextractor.extractor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.gittexplorer.documentextractor.configuration.DocumentExtractionProperties;
import org.junit.jupiter.api.Test;

class ApacheTikaTextExtractorTest {

    @Test
    void extractsTextWithTikaWhenOcrIsNotSupported() {
        ImageTextExtractionService imageTextExtractionService = mock(ImageTextExtractionService.class);
        when(imageTextExtractionService.supports("sample.txt", "text/plain")).thenReturn(false);

        ApacheTikaTextExtractor extractor = new ApacheTikaTextExtractor(imageTextExtractionService, new DocumentExtractionProperties());

        assertThat(extractor.extract("CUST12345678".getBytes(), "sample.txt"))
                .contains("CUST12345678");
    }
}
