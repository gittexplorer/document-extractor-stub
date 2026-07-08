package com.gittexplorer.documentextractor.extractor;

import com.gittexplorer.documentextractor.exception.DocumentExtractionException;
import java.io.ByteArrayInputStream;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;

@Service
public class ApacheTikaTextExtractor implements TextExtractionService {

    private final Tika tika;
    private final AutoDetectParser parser;
    private final ImageTextExtractionService imageTextExtractionService;

    public ApacheTikaTextExtractor(ImageTextExtractionService imageTextExtractionService) {
        this.tika = new Tika();
        this.parser = new AutoDetectParser();
        this.imageTextExtractionService = imageTextExtractionService;
    }

    @Override
    public String extract(byte[] content, String fileName) {
        String mimeType = detectMimeType(content, fileName);
        String tikaText = extractWithTika(content, fileName);
        if (!imageTextExtractionService.supports(fileName, mimeType)) {
            return tikaText;
        }

        String ocrText = imageTextExtractionService.extractText(content, fileName, mimeType);
        if (tikaText == null || tikaText.isBlank()) {
            return ocrText;
        }
        if (ocrText == null || ocrText.isBlank()) {
            return tikaText;
        }
        return tikaText.stripTrailing() + System.lineSeparator() + ocrText;
    }

    @Override
    public String detectMimeType(byte[] content, String fileName) {
        try {
            return tika.detect(content, fileName);
        } catch (Exception ex) {
            return "application/octet-stream";
        }
    }

    private String extractWithTika(byte[] content, String fileName) {
        Metadata metadata = new Metadata();
        metadata.set(Metadata.RESOURCE_NAME_KEY, fileName);

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content)) {
            BodyContentHandler handler = new BodyContentHandler(-1);
            parser.parse(inputStream, handler, metadata);
            return handler.toString();
        } catch (Exception ex) {
            throw new DocumentExtractionException("Unable to extract text", ex);
        }
    }
}
