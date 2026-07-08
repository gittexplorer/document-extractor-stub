package com.gittexplorer.documentextractor.util;

import com.gittexplorer.documentextractor.configuration.DocumentExtractionProperties;
import org.springframework.stereotype.Component;

@Component
public class TextNormalizationUtility {

    private final DocumentExtractionProperties properties;

    public TextNormalizationUtility(DocumentExtractionProperties properties) {
        this.properties = properties;
    }

    public String normalize(String text) {
        if (text == null) {
            return "";
        }
        DocumentExtractionProperties.TextNormalization config = properties.getTextNormalization();
        if (!config.isEnabled()) {
            return text;
        }

        String normalized = text.replace("\r\n", "\n").replace('\r', '\n');
        normalized = normalized.replaceAll("[\\t\\x0B\\f ]+", " ");
        normalized = normalized.replaceAll(" *\\n *", "\n");

        if (config.isCollapseWhitespace()) {
            normalized = normalized.replaceAll("\\s+", " ");
        } else {
            String blankLinePattern = "\\n{" + Math.max(2, config.getMaxConsecutiveLineBreaks() + 1) + ",}";
            String replacement = "\n".repeat(Math.max(1, config.getMaxConsecutiveLineBreaks()));
            normalized = normalized.replaceAll(blankLinePattern, replacement);
        }

        return normalized.trim();
    }
}
