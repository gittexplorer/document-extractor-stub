package com.gittexplorer.documentextractor.dto;
import java.time.Instant;import java.util.List;import java.util.Map;
public record DocumentExtractionResponse(String requestId, Instant timestamp, Summary summary, List<DocumentResult> documents) {
 public record Summary(int documentsReceived,int documentsProcessed,int documentsSucceeded,int documentsFailed,long totalUploadSize,long processingTimeMs) {}
 public record DocumentResult(String fileName,String mimeType,String fileExtension,long fileSize,String sha256,Instant extractionTimestamp,long characterCount,long processingTimeMs,String status,String text,Map<String,List<String>> identifiers,String error) {}
}
