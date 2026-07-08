package com.gittexplorer.documentextractor.extractor;

public interface ImageTextExtractionService {

    boolean supports(String fileName, String mimeType);

    String extractText(byte[] content, String fileName, String mimeType);
}
