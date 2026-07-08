package com.gittexplorer.documentextractor.service;
import com.gittexplorer.documentextractor.dto.DocumentExtractionResponse;import org.springframework.web.multipart.MultipartFile;
public interface DocumentService { DocumentExtractionResponse extract(MultipartFile[] files); }
