# Document Extractor Stub

A standalone Spring Boot 3 / Java 17 REST API that extracts text and metadata from local document uploads with Apache Tika, performs local OCR for raster image text extraction through Tesseract, then applies configurable identifier regular expressions.

## Features

- `POST /api/v1/documents/extract` multipart API for one or more `files` parts.
- Apache Tika based extraction for PDF, DOC, DOCX, TXT, RTF, HTML, XML, CSV, XLS, XLSX and ODT.
- Real raster image text extraction for JPG, JPEG, TIFF, BMP, PNG, GIF and AVIF through a local Tesseract OCR process.
- SVG text extraction through Apache Tika/XML parsing.
- SHA-256, MIME type, extension, size, timestamp, duration, character count, status and failure details per document.
- Identifier matching from `application.yml`; compiled once and reused.
- Configurable text normalization removes noisy OCR/Tika whitespace before the response is built.
- Upload bytes and MIME detection are reused during processing to avoid duplicate file reads.
- Concurrent processing via a configurable `ThreadPoolTaskExecutor`.
- Central JSON error handling and Swagger UI.

> Raster image text extraction uses a local Tesseract executable. No cloud OCR, AI service, database or Docker runtime is used by the application.

## Architecture

Packages follow a layered design:

- `controller` - REST resources.
- `service` and `service.impl` - orchestration and business services.
- `extractor` - Apache Tika abstraction and implementation.
- `regex` - dynamic identifier configuration and compiled regex cache.
- `validation` - upload and type validation.
- `exception` - application exceptions and `@RestControllerAdvice`.
- `dto`, `model`, `util`, `configuration` - API models, enums, helpers and properties.

## Build and run

```bash
mvn clean install
mvn spring-boot:run
```

For raster image OCR, install Tesseract locally and make the executable available on `PATH`, or set `document-extraction.ocr.tesseract-path` to the executable location.

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

## API

### Extract documents

```bash
curl -X POST "http://localhost:8080/api/v1/documents/extract" \
  -H "Accept: application/json" \
  -F "files=@samples/customer.txt;type=text/plain" \
  -F "files=@samples/policy.pdf;type=application/pdf"
```

Sample response:

```json
{
  "requestId": "b9b7c7d2-9bd7-4493-9bdb-d0226c9f7fd5",
  "timestamp": "2026-07-08T12:00:00Z",
  "summary": {
    "documentsReceived": 1,
    "documentsProcessed": 1,
    "documentsSucceeded": 1,
    "documentsFailed": 0,
    "totalUploadSize": 92,
    "processingTimeMs": 31
  },
  "documents": [
    {
      "fileName": "customer.txt",
      "mimeType": "text/plain",
      "fileExtension": "txt",
      "fileSize": 92,
      "sha256": "...",
      "characterCount": 58,
      "processingTimeMs": 15,
      "status": "SUCCESS",
      "text": "Customer CUST12345678 can be reached at abc@test.com.",
      "identifiers": {
        "CUSTOMER_ID": ["CUST12345678"],
        "EMAIL": ["abc@test.com"]
      }
    }
  ]
}
```

## Configuration

Limits and supported formats are configured in `src/main/resources/application.yml`.

```yaml
spring.servlet.multipart.max-file-size: 10MB
spring.servlet.multipart.max-request-size: 25MB

document-extraction:
  max-file-size: 10MB
  max-request-size: 25MB
  supported-extensions: [pdf, doc, docx, txt, png, jpg, jpeg, tiff]
  text-normalization:
    enabled: true
    collapse-whitespace: true
    max-consecutive-line-breaks: 1
  ocr:
    enabled: true
    tesseract-path: tesseract
    language: eng
    page-segmentation-mode: 6
    extract-image-metadata-with-tika: false
    timeout: 30s
```

Add identifier patterns without code changes:

```yaml
identifiers:
  definitions:
    INVOICE_ID:
      regex: "\\bINV\\d{8}\\b"
```

Regex definitions are validated and compiled during configuration binding; invalid expressions fail startup. By default, extracted text is normalized into readable single-line content so JSON responses do not contain long runs of escaped `\n` characters. Set `document-extraction.text-normalization.collapse-whitespace=false` to preserve paragraph line breaks with a configurable maximum.

## Supported formats

PDF, DOC, DOCX, TXT, RTF, HTML, XML, CSV, XLS, XLSX, ODT, JPG, JPEG, TIFF, BMP, SVG, PNG, GIF and AVIF. Raster image formats are passed to local Tesseract OCR after MIME/type validation.

## Performance tuning

The service reads each upload once, detects MIME type once, then reuses that context for validation, extraction, hashing and response metadata. Raster image OCR skips Apache Tika image metadata parsing by default because OCR is usually the expensive and useful step for image text extraction. If image metadata text is required, set `document-extraction.ocr.extract-image-metadata-with-tika=true`.

OCR speed depends heavily on image size, image quality, language packs and Tesseract page segmentation mode. For faster image processing, use the smallest acceptable upload resolution, configure only the languages needed, and tune `document-extraction.executor` so concurrency matches the CPU capacity of the host.

## Error handling

Errors use a consistent JSON shape with `requestId`, timestamp, HTTP status, message and details. Validation rejects empty files, null requests, unsupported types, oversized files and malformed multipart requests.

## Assumptions and limitations

- Extraction runs locally and keeps uploaded file bytes in memory only for the duration of each request.
- If extraction fails for one supported document, other documents continue and the failed item is returned with `status=FAILED`.
- Raster image OCR requires a local Tesseract installation. If the configured executable is unavailable or OCR times out, only that document is marked `FAILED` and the remaining documents continue processing.
