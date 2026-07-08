package com.gittexplorer.documentextractor;

import com.gittexplorer.documentextractor.configuration.DocumentExtractionProperties;
import com.gittexplorer.documentextractor.regex.RegexConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({DocumentExtractionProperties.class, RegexConfiguration.class})
public class DocumentExtractorApplication {
    public static void main(String[] args) { SpringApplication.run(DocumentExtractorApplication.class, args); }
}
