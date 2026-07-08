package com.gittexplorer.documentextractor.extractor;

import com.gittexplorer.documentextractor.configuration.DocumentExtractionProperties;
import com.gittexplorer.documentextractor.exception.DocumentExtractionException;
import com.gittexplorer.documentextractor.util.FileNameUtility;
import com.gittexplorer.documentextractor.util.ImageTypeUtility;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LocalTesseractImageTextExtractor implements ImageTextExtractionService {

    private static final Logger log = LoggerFactory.getLogger(LocalTesseractImageTextExtractor.class);

    private final DocumentExtractionProperties properties;
    private final FileNameUtility fileNameUtility;
    private final ImageTypeUtility imageTypeUtility;

    public LocalTesseractImageTextExtractor(
            DocumentExtractionProperties properties,
            FileNameUtility fileNameUtility,
            ImageTypeUtility imageTypeUtility) {
        this.properties = properties;
        this.fileNameUtility = fileNameUtility;
        this.imageTypeUtility = imageTypeUtility;
    }

    @Override
    public boolean supports(String fileName, String mimeType) {
        return properties.getOcr().isEnabled() && imageTypeUtility.isRasterImage(fileName, mimeType);
    }

    @Override
    public String extractText(byte[] content, String fileName, String mimeType) {
        if (!supports(fileName, mimeType)) {
            return "";
        }

        Path imageFile = null;
        Path outputBase = null;
        Path outputTextFile = null;
        Path processLogFile = null;
        try {
            imageFile = writeUploadToTemporaryFile(content, fileName);
            outputBase = Files.createTempFile("document-extractor-ocr-output-", "");
            Files.deleteIfExists(outputBase);
            outputTextFile = Path.of(outputBase.toString() + ".txt");
            processLogFile = Files.createTempFile("document-extractor-ocr-process-", ".log");

            ProcessBuilder processBuilder = new ProcessBuilder(buildCommand(imageFile, outputBase));
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectOutput(processLogFile.toFile());

            Process process = processBuilder.start();
            boolean completed = process.waitFor(properties.getOcr().getTimeout().toMillis(), TimeUnit.MILLISECONDS);
            String processOutput = Files.exists(processLogFile)
                    ? Files.readString(processLogFile, StandardCharsets.UTF_8).trim()
                    : "";
            if (!completed) {
                process.destroyForcibly();
                throw new DocumentExtractionException("OCR timed out while extracting text from image");
            }
            if (process.exitValue() != 0) {
                throw new DocumentExtractionException("OCR failed for image file: "
                        + fileNameUtility.safeName(fileName)
                        + summarizeProcessOutput(processOutput));
            }

            String extractedText = Files.exists(outputTextFile)
                    ? Files.readString(outputTextFile, StandardCharsets.UTF_8).trim()
                    : "";
            log.debug("OCR completed for file={} outputLength={}", fileNameUtility.safeName(fileName), extractedText.length());
            return extractedText;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new DocumentExtractionException("OCR interrupted while extracting text from image", ex);
        } catch (IOException ex) {
            throw new DocumentExtractionException("Unable to run local OCR process", ex);
        } finally {
            deleteTemporaryFile(imageFile);
            deleteTemporaryFile(outputTextFile);
            deleteTemporaryFile(outputBase);
            deleteTemporaryFile(processLogFile);
        }
    }

    private Path writeUploadToTemporaryFile(byte[] content, String fileName) throws IOException {
        String extension = fileNameUtility.extensionOf(fileName);
        String suffix = extension.isBlank() ? ".img" : "." + extension;
        Path imageFile = Files.createTempFile("document-extractor-ocr-", suffix);
        Files.write(imageFile, content);
        return imageFile;
    }

    private List<String> buildCommand(Path imageFile, Path outputBase) {
        DocumentExtractionProperties.Ocr ocr = properties.getOcr();
        List<String> command = new ArrayList<>();
        command.add(ocr.getTesseractPath());
        command.add(imageFile.toAbsolutePath().toString());
        command.add(outputBase.toAbsolutePath().toString());
        command.add("-l");
        command.add(ocr.getLanguage());
        command.add("--psm");
        command.add(String.valueOf(ocr.getPageSegmentationMode()));
        command.addAll(ocr.getAdditionalArguments());
        return command;
    }

    private String summarizeProcessOutput(String processOutput) {
        if (processOutput == null || processOutput.isBlank()) {
            return "";
        }
        return ": " + processOutput.substring(0, Math.min(processOutput.length(), 200));
    }

    private void deleteTemporaryFile(Path file) {
        if (file == null) {
            return;
        }
        try {
            Files.deleteIfExists(file);
        } catch (IOException ex) {
            log.warn("Unable to delete OCR temporary file: {}", file);
        }
    }
}
