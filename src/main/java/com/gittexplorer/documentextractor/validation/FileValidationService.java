package com.gittexplorer.documentextractor.validation;
import org.springframework.web.multipart.MultipartFile;
public interface FileValidationService { void validate(MultipartFile[] files); void validateSupported(String fileName, String mimeType); }
