package com.gittexplorer.documentextractor.dto;
import java.time.Instant;import java.util.List;
public record ErrorResponse(String requestId, Instant timestamp, int status, String error, String message, List<String> details) {}
