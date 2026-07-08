package com.gittexplorer.documentextractor.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.gittexplorer.documentextractor.configuration.DocumentExtractionProperties;
import org.junit.jupiter.api.Test;

class TextNormalizationUtilityTest {

    @Test
    void collapsesOcrWhitespaceToReadableSingleLineText() {
        TextNormalizationUtility utility = new TextNormalizationUtility(new DocumentExtractionProperties());

        String normalized = utility.normalize("\n\nVPD Platform\n\nUser Interface & Notification\n\n   Submit   approval\n");

        assertThat(normalized).isEqualTo("VPD Platform User Interface & Notification Submit approval");
    }

    @Test
    void canPreserveLimitedLineBreaksWhenCollapseWhitespaceIsDisabled() {
        DocumentExtractionProperties properties = new DocumentExtractionProperties();
        properties.getTextNormalization().setCollapseWhitespace(false);
        properties.getTextNormalization().setMaxConsecutiveLineBreaks(1);
        TextNormalizationUtility utility = new TextNormalizationUtility(properties);

        String normalized = utility.normalize("Header\n\n\nBody");

        assertThat(normalized).isEqualTo("Header\nBody");
    }
}
