package com.gittexplorer.documentextractor.extractor;

import com.gittexplorer.documentextractor.exception.DocumentExtractionException;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
public class ApacheTikaTextExtractor implements TextExtractionService {
    private final Tika tika = new Tika();
    private final AutoDetectParser parser = new AutoDetectParser();

    public String extract(byte[] content, String fileName) {
        Metadata md = new Metadata();
        // Use the string key so the code compiles regardless of which Tika constants are present.
        md.set("resourceName", fileName);
        try (var in = new ByteArrayInputStream(content)) {
            BodyContentHandler handler = new BodyContentHandler(-1);
            parser.parse(in, handler, md);
            return handler.toString();
        } catch (Exception e) {
            throw new DocumentExtractionException("Unable to extract text", e);
        }
    }

    public String detectMimeType(byte[] content, String fileName) {
        try {
            return tika.detect(content, fileName);
        } catch (Exception e) {
            return "application/octet-stream";
        }
    }
}