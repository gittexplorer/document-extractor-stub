package com.gittexplorer.documentextractor.service.impl;

import com.gittexplorer.documentextractor.dto.DocumentExtractionResponse;
import com.gittexplorer.documentextractor.exception.DocumentExtractionException;
import com.gittexplorer.documentextractor.exception.InvalidUploadException;
import com.gittexplorer.documentextractor.extractor.TextExtractionService;
import com.gittexplorer.documentextractor.model.DocumentStatus;
import com.gittexplorer.documentextractor.service.DocumentService;
import com.gittexplorer.documentextractor.service.RegexExtractionService;
import com.gittexplorer.documentextractor.util.FileNameUtility;
import com.gittexplorer.documentextractor.util.HashUtility;
import com.gittexplorer.documentextractor.util.TextNormalizationUtility;
import com.gittexplorer.documentextractor.validation.FileValidationService;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentServiceImpl implements DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);

    private final FileValidationService validation;
    private final TextExtractionService extractor;
    private final RegexExtractionService regex;
    private final HashUtility hash;
    private final FileNameUtility names;
    private final TextNormalizationUtility textNormalizationUtility;
    private final Executor executor;

    public DocumentServiceImpl(
            FileValidationService validation,
            TextExtractionService extractor,
            RegexExtractionService regex,
            HashUtility hash,
            FileNameUtility names,
            TextNormalizationUtility textNormalizationUtility,
            @Qualifier("documentTaskExecutor") Executor executor) {
        this.validation = validation;
        this.extractor = extractor;
        this.regex = regex;
        this.hash = hash;
        this.names = names;
        this.textNormalizationUtility = textNormalizationUtility;
        this.executor = executor;
    }

    @Override
    public DocumentExtractionResponse extract(MultipartFile[] files) {
        long started = System.nanoTime();
        String requestId = UUID.randomUUID().toString();
        validation.validate(files);

        List<UploadContext> uploads = Arrays.stream(files).map(this::prepareUpload).toList();
        long totalUploadSize = uploads.stream().mapToLong(UploadContext::fileSize).sum();

        log.info("requestId={} documents={} totalUploadSize={}", requestId, uploads.size(), totalUploadSize);

        List<DocumentExtractionResponse.DocumentResult> documents = uploads.stream()
                .map(upload -> CompletableFuture.supplyAsync(() -> process(requestId, upload), executor))
                .map(CompletableFuture::join)
                .toList();

        int succeeded = (int) documents.stream()
                .filter(document -> DocumentStatus.SUCCESS.name().equals(document.status()))
                .count();
        long processingTimeMs = (System.nanoTime() - started) / 1_000_000;

        var summary = new DocumentExtractionResponse.Summary(
                files.length,
                documents.size(),
                succeeded,
                documents.size() - succeeded,
                totalUploadSize,
                processingTimeMs);
        return new DocumentExtractionResponse(requestId, Instant.now(), summary, documents);
    }

    private UploadContext prepareUpload(MultipartFile file) {
        String fileName = names.safeName(file.getOriginalFilename());
        try {
            byte[] bytes = file.getBytes();
            String mimeType = extractor.detectMimeType(bytes, fileName);
            validation.validateSupported(fileName, mimeType);
            return new UploadContext(fileName, names.extensionOf(fileName), file.getSize(), bytes, mimeType);
        } catch (IOException ex) {
            throw new InvalidUploadException("Unable to read uploaded file: " + file.getOriginalFilename());
        }
    }

    private DocumentExtractionResponse.DocumentResult process(String requestId, UploadContext upload) {
        long started = System.nanoTime();
        try {
            String text = textNormalizationUtility.normalize(extractor.extract(upload.content(), upload.fileName()));
            long processingTimeMs = (System.nanoTime() - started) / 1_000_000;

            log.info(
                    "requestId={} file={} size={} durationMs={} status=SUCCESS",
                    requestId,
                    upload.fileName(),
                    upload.fileSize(),
                    processingTimeMs);

            return new DocumentExtractionResponse.DocumentResult(
                    upload.fileName(),
                    upload.mimeType(),
                    upload.fileExtension(),
                    upload.fileSize(),
                    hash.sha256(upload.content()),
                    Instant.now(),
                    text.length(),
                    processingTimeMs,
                    DocumentStatus.SUCCESS.name(),
                    text,
                    regex.extractIdentifiers(text),
                    null);
        } catch (Exception ex) {
            long processingTimeMs = (System.nanoTime() - started) / 1_000_000;
            String message = ex instanceof DocumentExtractionException ? ex.getMessage() : "Unable to process document";
            log.warn(
                    "requestId={} file={} size={} durationMs={} status=FAILED reason={}",
                    requestId,
                    upload.fileName(),
                    upload.fileSize(),
                    processingTimeMs,
                    ex.getMessage());

            return new DocumentExtractionResponse.DocumentResult(
                    upload.fileName(),
                    upload.mimeType(),
                    upload.fileExtension(),
                    upload.fileSize(),
                    null,
                    Instant.now(),
                    0,
                    processingTimeMs,
                    DocumentStatus.FAILED.name(),
                    null,
                    Map.of(),
                    message);
        }
    }

    private record UploadContext(String fileName, String fileExtension, long fileSize, byte[] content, String mimeType) {
    }
}
