package com.gittexplorer.documentextractor.controller;

import com.gittexplorer.documentextractor.dto.DocumentExtractionResponse;
import com.gittexplorer.documentextractor.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
class DocumentControllerTest {
    @Autowired
    MockMvc mvc;
    @MockBean
    DocumentService service;

    @Test
    void extractsMultipartFiles() throws Exception {
        when(service.extract(any())).thenReturn(new DocumentExtractionResponse("id", Instant.now(), new DocumentExtractionResponse.Summary(1, 1, 1, 0, 4, 1), List.of()));
        MockMultipartFile file = new MockMultipartFile("files", "a.txt", MediaType.TEXT_PLAIN_VALUE, "text".getBytes());
        mvc.perform(multipart("/api/v1/documents/extract").file(file)).andExpect(status().isOk());
    }
}
