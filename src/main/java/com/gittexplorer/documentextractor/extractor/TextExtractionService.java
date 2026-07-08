package com.gittexplorer.documentextractor.extractor;
public interface TextExtractionService { String extract(byte[] content, String fileName) ; String detectMimeType(byte[] content, String fileName); }
