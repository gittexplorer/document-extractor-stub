package com.gittexplorer.documentextractor.configuration;

import java.util.LinkedHashSet;
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
    public DataSize getMaxFileSize() { return maxFileSize; }
    public void setMaxFileSize(DataSize maxFileSize) { this.maxFileSize = maxFileSize; }
    public DataSize getMaxRequestSize() { return maxRequestSize; }
    public void setMaxRequestSize(DataSize maxRequestSize) { this.maxRequestSize = maxRequestSize; }
    public Set<String> getSupportedExtensions() { return supportedExtensions; }
    public void setSupportedExtensions(Set<String> supportedExtensions) { this.supportedExtensions = supportedExtensions; }
    public Set<String> getSupportedMimeTypes() { return supportedMimeTypes; }
    public void setSupportedMimeTypes(Set<String> supportedMimeTypes) { this.supportedMimeTypes = supportedMimeTypes; }
    public Executor getExecutor() { return executor; }
    public void setExecutor(Executor executor) { this.executor = executor; }
    public static class Executor {
        private int corePoolSize = 4; private int maxPoolSize = 8; private int queueCapacity = 50; private String threadNamePrefix = "doc-extract-";
        public int getCorePoolSize() { return corePoolSize; } public void setCorePoolSize(int corePoolSize) { this.corePoolSize = corePoolSize; }
        public int getMaxPoolSize() { return maxPoolSize; } public void setMaxPoolSize(int maxPoolSize) { this.maxPoolSize = maxPoolSize; }
        public int getQueueCapacity() { return queueCapacity; } public void setQueueCapacity(int queueCapacity) { this.queueCapacity = queueCapacity; }
        public String getThreadNamePrefix() { return threadNamePrefix; } public void setThreadNamePrefix(String threadNamePrefix) { this.threadNamePrefix = threadNamePrefix; }
    }
}
