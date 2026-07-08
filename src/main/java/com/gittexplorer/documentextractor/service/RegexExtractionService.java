package com.gittexplorer.documentextractor.service;
import java.util.List;import java.util.Map;
public interface RegexExtractionService { Map<String, List<String>> extractIdentifiers(String text); }
