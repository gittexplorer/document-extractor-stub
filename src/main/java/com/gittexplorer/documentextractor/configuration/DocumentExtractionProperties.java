package com.gittexplorer.documentextractor.configuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties(prefix = "document-extraction")
public class DocumentExtractionProperties {

    private DataSize maxFileSize = DataSize.ofMegabytes(10);
    private DataSize maxRequestSize = DataSize.ofMegabytes(25);
    private Set<String> supportedExtensions = new LinkedHashSet<>();
    private Set<String> supportedMimeTypes = new LinkedHashSet<>();
    private Executor executor = new Executor();
    private Ocr ocr = new Ocr();
    private TextNormalization textNormalization = new TextNormalization();

    public DataSize getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(DataSize maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public DataSize getMaxRequestSize() {
        return maxRequestSize;
    }

    public void setMaxRequestSize(DataSize maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }

    public Set<String> getSupportedExtensions() {
        return supportedExtensions;
    }

    public void setSupportedExtensions(Set<String> supportedExtensions) {
        this.supportedExtensions = supportedExtensions;
    }

    public Set<String> getSupportedMimeTypes() {
        return supportedMimeTypes;
    }

    public void setSupportedMimeTypes(Set<String> supportedMimeTypes) {
        this.supportedMimeTypes = supportedMimeTypes;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public Ocr getOcr() {
        return ocr;
    }

    public void setOcr(Ocr ocr) {
        this.ocr = ocr;
    }

    public TextNormalization getTextNormalization() {
        return textNormalization;
    }

    public void setTextNormalization(TextNormalization textNormalization) {
        this.textNormalization = textNormalization;
    }

    public static class Executor {
        private int corePoolSize = 4;
        private int maxPoolSize = 8;
        private int queueCapacity = 50;
        private String threadNamePrefix = "doc-extract-";

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public int getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public int getQueueCapacity() {
            return queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }

        public String getThreadNamePrefix() {
            return threadNamePrefix;
        }

        public void setThreadNamePrefix(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
        }
    }

    public static class TextNormalization {
        private boolean enabled = true;
        private boolean collapseWhitespace = true;
        private int maxConsecutiveLineBreaks = 1;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isCollapseWhitespace() {
            return collapseWhitespace;
        }

        public void setCollapseWhitespace(boolean collapseWhitespace) {
            this.collapseWhitespace = collapseWhitespace;
        }

        public int getMaxConsecutiveLineBreaks() {
            return maxConsecutiveLineBreaks;
        }

        public void setMaxConsecutiveLineBreaks(int maxConsecutiveLineBreaks) {
            this.maxConsecutiveLineBreaks = maxConsecutiveLineBreaks;
        }
    }

    public static class Ocr {
        private boolean enabled = true;
        private String tesseractPath = "tesseract";
        private String language = "eng";
        private int pageSegmentationMode = 6;
        private boolean extractImageMetadataWithTika = false;
        private Duration timeout = Duration.ofSeconds(30);
        private List<String> additionalArguments = new ArrayList<>();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getTesseractPath() {
            return tesseractPath;
        }

        public void setTesseractPath(String tesseractPath) {
            this.tesseractPath = tesseractPath;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public int getPageSegmentationMode() {
            return pageSegmentationMode;
        }

        public void setPageSegmentationMode(int pageSegmentationMode) {
            this.pageSegmentationMode = pageSegmentationMode;
        }

        public boolean isExtractImageMetadataWithTika() {
            return extractImageMetadataWithTika;
        }

        public void setExtractImageMetadataWithTika(boolean extractImageMetadataWithTika) {
            this.extractImageMetadataWithTika = extractImageMetadataWithTika;
        }

        public Duration getTimeout() {
            return timeout;
        }

        public void setTimeout(Duration timeout) {
            this.timeout = timeout;
        }

        public List<String> getAdditionalArguments() {
            return additionalArguments;
        }

        public void setAdditionalArguments(List<String> additionalArguments) {
            this.additionalArguments = additionalArguments;
        }
    }
}
